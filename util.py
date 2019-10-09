import random


def convert_date(date: str):
    split_date = date.split("-")
    month = int(split_date[1])
    day = int(split_date[2])

    month_arr = [int(d) for d in str(bin(month))[2:]]
    if len(month_arr) < 4:
        while len(month_arr) != 4:
            month_arr.insert(0, 0)

    day_arr = [int(d) for d in str(bin(day))[2:]]
    if len(day_arr) < 5:
        while len(day_arr) != 5:
            day_arr.insert(0, 0)

    return month_arr + day_arr


def batches(data, batch_size):
    random.shuffle(data)
    batches = []
    index = 0
    while index + batch_size <= len(data):
        batches.append(data[index : index + batch_size])
        index += batch_size

    for batch in batches:
        yield batch


def read_data():
    data = []
    with open("./data.txt") as file:
        for line in file:
            split_line = line.split(": ")
            date = split_line[0]
            rain = 1 if float(split_line[1]) > 0 else 0
            data.append( (convert_date(date), [rain]) )

    return data


def save_dataset():
    data = read_data()
    batch_list = [batch for batch in batches(data, 443)]
    train_batches = batch_list[1:]
    test_batches = batch_list[0:1]

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

    for i in range(len(x_train)):
        with open("./data/train/x_train_" + str(i) + ".txt", "w") as file:
            for input in x_train[i]:
                file.write(str(input)[1:-1] + "\n")
        with open("./data/train/y_train_" + str(i) + ".txt", "w") as file:
            for output in y_train[i]:
                file.write(str(output)[1:-1] + "\n")

    for i in range(len(x_test)):
        with open("./data/test/x_test_" + str(i) + ".txt", "w") as file:
            for input in x_test[i]:
                file.write(str(input)[1:-1] + "\n")
        with open("./data/test/y_test_" + str(i) + ".txt", "w") as file:
            for output in y_test[i]:
                file.write(str(output)[1:-1] + "\n")


if __name__ == "__main__":
    save_dataset()

