import tensorflow as tf
from tensorflow import keras
from util import *


class FeedForwardModel(keras.Model):

    def __init__(self):
        super(FeedForwardModel, self).__init__(self)
        self.dense1 = keras.layers.Dense(54, input_shape = [9], use_bias = True, activation = "relu")
        self.dense2 = keras.layers.Dense(1, use_bias = True, activation = "sigmoid")

    
    @tf.function
    def __call__(self, input_data):
        x = self.dense1(input_data)
        return self.dense2(x)


if __name__ == "__main__":
    data = read_data()
    batches = [batch for batch in batches(data, 443)]
    train_batches = batches[1:]
    test_batches = batches[0:1]

    x_train, y_train = [], []
    x_test, y_test = [], []

    for batch in train_batches:
        x_train_temp, y_train_temp = zip(*batch)
        x_train.append(x_train_temp)
        y_train.append(y_train_temp)

    for batch in test_batches:
        x_test_temp, y_test_temp = zip(*batch)
        x_test.append(x_test_temp)
        y_test.append(y_test_temp)

    # model = FeedForwardModel()
    model = keras.models.Sequential([
        keras.layers.Dense(54, use_bias = True, activation = "relu"),
        keras.layers.Dense(1, use_bias = True, activation = "sigmoid")
    ])
    model.compile(optimizer = "adam", loss = "huber", metrics = ["accuracy"])
    model.fit(x_train, y_train, epochs = 5)
    model.evaluate(x_test, y_test)
    
