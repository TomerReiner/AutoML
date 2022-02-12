import pandas as pd
from sklearn.base import BaseEstimator
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
from sklearn.tree import DecisionTreeRegressor
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import RandomForestRegressor
from sklearn.linear_model import LinearRegression
from sklearn.linear_model import ElasticNetCV
from sklearn.svm import SVC
from sklearn.svm import SVR
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.naive_bayes import MultinomialNB
from sklearn.naive_bayes import ComplementNB
from sklearn.naive_bayes import BernoulliNB


class MyML:
    """
    This class will build ML Model on the data.

    :param X_train: pd.DataFrame, the training X data.
    :param X_test: pd.DataFrame, the testing X data.
    :param y_train: pd.Series, the training y data.
    :param y_train: pd.Series, the testing y data.
    :param ml_model: The ML Model.
    """
    def __init__(self, df: pd.DataFrame, y_column: str):
        y = df[y_column]
        X = df.drop(y_column, axis=1)
        self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(X, y.values.reshape(-1, 1))
        self.ml_model = BaseEstimator()

    def build_ml_model(self, ml_model_name: str, ml_model_config: dict):
        """
        This function builds ML Model according to the configuration.
        :param ml_model_name: The name of the ml model.
        :param ml_model_config: The configuration of the ml model (i.e. parameters of the ml model.)
        """

        if ml_model_name == "Decision Tree Classifier":
            self.ml_model = DecisionTreeClassifier(**ml_model_config)

        if ml_model_name == "Decision Tree Regressor":
            self.ml_model = DecisionTreeRegressor(**ml_model_config)

        if ml_model_name == "Random Forest Classifier":
            self.ml_model = RandomForestClassifier(**ml_model_config)

        if ml_model_name == "Random Forest Regressor":
            self.ml_model = RandomForestRegressor(**ml_model_config)

        if ml_model_name == "Linear Regression":
            self.ml_model = LinearRegression(**ml_model_config)

        if ml_model_name == "Elastic Net CV":
            self.ml_model = ElasticNetCV(**ml_model_config)

        if ml_model_name == "SVC":
            self.ml_model = SVC(**ml_model_config)

        if ml_model_name == "SVR":
            self.ml_model = SVR(**ml_model_config)

        if ml_model_name == "K Nearest Neighbors Classifier":
            self.ml_model = KNeighborsClassifier(**ml_model_config)

        if ml_model_name == "Gaussian NB":
            self.ml_model = GaussianNB(**ml_model_config)

        if ml_model_name == "Multinomial NB":
            self.ml_model = MultinomialNB(**ml_model_config)

        if ml_model_name == "Complement NB":
            self.ml_model = ComplementNB(**ml_model_config)

        if ml_model_name == "Bernoulli NB":
            self.ml_model = BernoulliNB(**ml_model_config)

        self.ml_model.fit(self.X_train, self.y_train)

    def score(self):
        """
        This function predicts a `y` value for each X value, and computes the score of the ml model.
        :return: The score of the ml model for the predictions.
        """
        return self.ml_model.score(self.X_test, self.y_test)

    def predict(self, X: pd.Series):
        """
        This function predicts a single y value for some X values.
        :param X: The x values.
        :return: np.array, the predicted y value.
        """
        return self.ml_model.predict(X)

