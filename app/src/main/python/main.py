from data_preprocessing import DataPreprocessor
from ml import MyML
import pandas as pd
import numpy as np

class MLModel:

    """
    This class will build the ML Model.
    :param columns: Java array, the columns in the dataset.
    :param data: Java 2D array, the data in the dataset.
    :param blocks: java.util.ArrayList, The blocks of actions in the dataset.
    :param ml_model_name: The name of the ml model.
    :param ml_model_config: The configuration for the model.
    :param y_column: str, the y column.
    """

    def __init__(self, columns, data, blocks, ml_model_name, ml_model_config, y_column):
        self.df = self.convert_java_data_to_dataframe(columns, data)
        self.blocks = blocks
        self.ml_model_name = ml_model_name
        self.ml_model_config = self.convert_ml_model_config_to_dict(ml_model_config)
        self.y_column = y_column
        self.preprocessor = DataPreprocessor(self.df)

        self.y_column_encoding = {}  # This parameter will be used to save the numerical representation of the y column value in case the task is a classification class.

    def convert_java_data_to_dataframe(self, columns, data) -> pd.DataFrame:
        """
        This function converts a dataset of type java.util.HashMap to python dict.
        :param columns: Java with the column names.
        :param data: Java array with the data.
        :return: The dataset in a pd.DataFrame.
        """

        columns = np.array(columns).tolist()
        data = np.array(data).tolist()

        d = {}

        for i in range(len(columns)):  # Convert all the data into a dictionary to pass it into the dataframe.
            d[columns[i]] = data[i]

        df = pd.DataFrame(d)
        return df

    def convert_ml_model_config_to_dict(self, ml_model_config):
        """
        This function converts the configuration of ML Model to python dict.
        :param ml_model_config: java.util.HashMap, the configuration of the ML Model.
        :return: dict, the configuration of the model in a python dict.
        """

        d = {}

        keys = np.array(ml_model_config.keySet().toArray())

        for key in keys:
            d[key] = ml_model_config.get(str(key))

        return d

    def pipeline(self):

        if self.ml_model_name not in ["Decision Tree Regressor", "Random Forest Regressor", "Linear Regression", "Elastic Net CV", "SVR"]:
            self.y_column_encoding = self.preprocessor.encode_y_column(self.y_column)

        """
        This function will create a pipeline for preprocessing the data and building the ml model.
        :return: The score of the ML Model, the number of columns in the dataset,
         the number of rows in the dataset, the encoded values of the y column(if this is a classification task)
         and the ml model that was built. The last two parameters will be used to predict new data.
         in case everything went smoothly,
        otherwise it will return an error message.
        """
        for i in range(self.blocks.size()):  # Preparing the dataset.
            block = self.blocks.get(i)
            block_attributes = block.getAttributes()

            action = block_attributes.get("action")  # The data preprocessing action the user would like to do.
            column = block_attributes.get("column")  # Get the relevant column for the preprocessing action.

            if action == "Fill NA":  # Fill Na values in a specific column.
                method = block_attributes.get("Fill NA")  # Get the fill NA method
                self.preprocessor.fill_na(column, method)

            elif action == "Normalize Column":  # Normalize a column.
                self.preprocessor.normalize(column)

            elif action == "Encode Column":  # Encode a column.
                self.preprocessor.encode(column)

            else:  # If the condition has reached this point then the user has select the drop NA action.
                self.preprocessor.drop_na()

        valid, message = self.preprocessor.check_valid()

        if not valid:  # If the data is not valid, then it means the dataset has NaN values/text values.
            return message  # The error message.
        print(self.preprocessor.df)
        print(self.preprocessor.df.columns, self.preprocessor.df.iloc[0])
        self.ml = MyML(self.preprocessor.df, self.y_column)

        self.ml.build_ml_model(self.ml_model_name, self.ml_model_config)  # Build the ML Model.
        return self.ml.score(), len(self.preprocessor.df.columns), len(self.preprocessor.df), self.y_column_encoding, self.ml.ml_model, self.preprocessor.normalization_info, self.preprocessor.df.columns.values


def test_ml(data, ml_model, y_column_encoding): # TODO - add normalization info.
    """
    This function tests the ml model on new data that the user has inserted.
    :param data: a double array with the data.
    :param ml_model: The ML Model to test.
    :param y_column_encoding: The encoding of the y column.
    """
    if y_column_encoding == {}: # If this is a regression task.
        return ml_model.predict([np.array(data)])
    return y_column_encoding[ml_model.predict([np.array(data)])[0]]


def main(columns, data, blocks, ml_model_name, ml_model_config, y_column):
    ml_model = MLModel(columns, data, blocks, ml_model_name, ml_model_config, y_column)
    return ml_model.pipeline()
