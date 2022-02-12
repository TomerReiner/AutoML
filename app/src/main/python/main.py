from data_preprocessing import DataPreprocessor
from ml import MyML
import pandas as pd
import numpy as np


def convert_java_data_to_dataframe(columns, data) -> pd.DataFrame:
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

def convert_ml_model_config_to_dict(ml_model_config):
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


def pipeline(columns, data, blocks, ml_model_name, ml_model_config, y_column):
    """
    This function will create a pipeline for preprocessing the data and building the ml model.
    :param columns: Java array, the columns in the dataset.
    :param data: Java 2D array, the data in the dataset.
    :param blocks: java.util.ArrayList, The blocks of actions in the dataset.
    :param ml_model_name: The name of the ml model.
    :param ml_model_config: The configuration for the model.
    :param y_column: str, the y column.
    :return:
    """

    df = convert_java_data_to_dataframe(columns, data)

    preprocessor = DataPreprocessor(df)

    ml_model_config = convert_ml_model_config_to_dict(ml_model_config)

    for i in range(blocks.size()):  # Preparing the dataset.

        block = blocks.get(i)
        block_attributes = block.getAttributes()

        action = block_attributes.get("action")  # The data preprocessing action the user would like to do.
        column = block_attributes.get("column")  # Get the relevant column for the preprocessing action.

        if action == "Fill NA":  # Fill Na values in a specific column.
            method = block_attributes.get("Fill NA")  # Get the fill NA method
            preprocessor.fill_na(column, method)

        elif action == "Normalize Column":  # Normalize a column.
            preprocessor.normalize(column)

        elif action == "Encode Column":  # Encode a column.
            preprocessor.encode(column)

        else:  # If the condition has reached this point then the user has select the drop NA action.
            preprocessor.drop_na()

    valid, message = preprocessor.check_valid()

    if not valid:  # If the data is not valid, then it means the dataset has NaN values/text values.
        return message  # The error message.

    ml = MyML(preprocessor.df, y_column)

    ml.build_ml_model(ml_model_name, ml_model_config)
    return ml.score()

