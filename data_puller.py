from lxml import html
import requests
from datetime import timedelta, date
from math import *


LATITUDE = 43.149242
LONGITUDE = -77.605583
RADIUS = .111482


def daterange(start_date, end_date):
    for n in range(int ((end_date - start_date).days)):
        yield start_date + timedelta(n)


def get_precipitation(precip_data):
    # Strip request content of whitespace and order into tuples of data in format (precipitation, latitude, longitude)
    data = [", ".join(point.strip().split()) \
        for point in precip_data][6:len(precip_data) - 1] # [6:last] to get rid of the file header and last empty line
    data = [ ( float(data[0]), float(data[1]), float(data[2]) ) for data in (point.split(", ") for point in data) ]
        
    # Determine data point in ROC else 0
    for point in data:
        a = (LATITUDE - point[1])
        b = (LONGITUDE - point[2])
        distance = sqrt(a**2 + b**2)

        if distance <= RADIUS:
            return point[0]

    return 0


def main():
    header = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36"}
    
    # Get formatted dates
    start_date = date(2008, 5, 12)
    end_date = date(2019, 10, 2)

    roc_data = []
    for single_date in daterange(start_date, end_date):
        date_str = single_date.strftime("%Y-%m-%d")
        print("Processing " + date_str + "...")

        date_url = "".join(date_str.split("-"))
        page = requests.get("https://www.wpc.ncep.noaa.gov/qpf/obsmaps/p24i_" + date_url + "_sortbyvalue.txt", stream = True, headers = header)
        precip_data = page.text.split("\n")
        
        if precip_data[0][0] != "<":  # If the page is not found, the first char will be an opening HTML tag
            precip = get_precipitation(precip_data)
            roc_data.append((date_str, precip))

    # Save points to data.txt
    with open("data.txt", "w") as file:
        for (date_str, precip) in roc_data:
            file.write(date_str + ": " + str(precip) + "\n")
    

if __name__ == "__main__":
    main()