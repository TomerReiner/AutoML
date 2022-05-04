# Import all the required modules.
import numpy as np
import pandas as pd
from ml import MyML
from sklearn.preprocessing import MinMaxScaler
from sklearn.preprocessing import LabelEncoder

UNKNOWN = "UNKNOWN"  # Fill NA values in text column.

class DataPreprocessor:
    """
    This class is responsible for preprocessing the data.
    :param df: pd.DataFrame, the dataset.
    """

    def __init__(self, df: pd.DataFrame):
        self.df = df
        self.normalization_info = {}

    def normalize(self, column: str):
        """
        This function normalizes a column.
        :param column: The column in the dataset that the user wants to normalize.
        If the type of the column is `object`, the column will not be altered.
        """

        if self.df[column].dtype != "object":
            self.normalization_info[column] = [self.df[column].min(), self.df[column].max()]
            scaler = MinMaxScaler()
            self.df[column] = scaler.fit_transform(self.df[column].values.reshape(-1, 1))  # Normalize the values in the column.

    def encode(self, column: str):
        """
        This function encodes a column to a numerical representation.
        :param column: The column in the dataset that the user wants to encode.
        If the type of the column is not `object`, the column will not be altered.

        Example
        --------

        >>> d = {"phone_company" : ["Samsung", "Apple", "Xiaomi", "Samsung"], "price" : [980, 999, 540, 600]}
        >>> preprocessor = DataPreprocessor(d)
        >>> preprocessor.df
          phone_company  price
        0       Samsung    980
        1         Apple    999
        2        Xiaomi    540
        3       Samsung    600

        >>> preprocessor.encode("phone_company")
        >>> preprocessor.df
          phone_company  price
        0              1    980
        1              0    999
        2              2    540
        3              1    600
        """

        try:  # Check if the column can be a numerical column.
            self.df[column] = self.df[column].astype(float)
            self.normalization_info[column] = [self.df[column].min(), self.df[column].max()]

        except:  # The conversion has failed, therefore the column should be encoded.
            encoder = LabelEncoder()
            self.df[column] = encoder.fit_transform(self.df[column].values.reshape(-1, 1))
            self.normalization_info[column] = [self.df[column].min(), self.df[column].max()]

    def drop_na(self):
        """
        This function drops all the NA values in `df`
        """
        self.df = self.df.dropna()

    def fill_na(self, column: str, method: str):
        """
        This function fill the NA values according to a specific method.
        :param column: The column that the user wants to fill the NA values in.
        :param method: The filling method. If `self.df[column].dtype == "object"`,
         the NA values will be filled with `UNKNOWN`. Otherwise, the user can choose 3 methods:
         `Max Value` : The NA values in the column will be filled with the max value in it.
         `Min Value` : The NA values in the column will be filled with the min value in it.
         `Average Value` : The NA values in the column will be filled with the average value in it.
        """

        if self.df[column].dtype == "object":
            self.df[column] = self.df[column].fillna(UNKNOWN)
        else:
            if method == "Max Value":
                self.df[column] = self.df[column].fillna(self.df[column].max())
            elif method == "Min Value":
                self.df[column] = self.df[column].fillna(self.df[column].min())
            else:
                self.df[column] = self.df[column].fillna(self.df[column].mean())

    def check_valid(self):
        """
        This function prepares the data for the ML model. The function checks if there are only numerical values in the column
        and if all the values are empty.
        :return: tuple, with 2 items. The first item is a flag (`True` if the `df` is ready to be fit into the model, `False` otherwise).
            The second item is an error message, if the flag is `False`, for example: "The column `price` has NA values.".
            If the flag is `True`, the second item will be an empty string.
        """

        columns = self.df.columns

        for column in columns:
            self.encode(column)  # Encode all the columns that haven't been encoded yet.

            if self.df[column].hasnans:
                return False, f"The column `{column}` has NaN values."

        return True, ""  # The dataset is valid.


    def encode_y_column(self, y_column_name: str) -> dict:
        y = self.df[y_column_name]

        encoder = LabelEncoder()

        encoder.fit(y)

        classes = list(encoder.classes_)

        return {x: classes[x] for x in range(len(classes))}


