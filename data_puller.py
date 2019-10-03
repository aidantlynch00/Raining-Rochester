from lxml import html
import requests
from datetime import timedelta, date
from math import *


LATITUDE = 43.161030
LONGITUDE = -77.610924


def daterange(start_date, end_date):
    for n in range(int ((end_date - start_date).days)):
        yield start_date + timedelta(n)


def main():
    header = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36"}
    
    # Get formatted dates
    start_date = date(2008, 5, 4)
    end_date = date(2019, 10, 2)
    for single_date in daterange(start_date, end_date):
        date_str = single_date.strftime("%Y-%m-%d")

    page = requests.get("https://www.wpc.ncep.noaa.gov/qpf/obsmaps/p24i_20191002_sortbyvalue.txt", stream = True, headers = header)
    precip_data = page.text.split("\n")
    
    # Strip request content of whitespace and order into tuples of data in format (precipitation, latitude, longitude)
    data = [", ".join(point.strip().split()) \
        for point in precip_data][6:len(precip_data) - 1] # [6:last] to get rid of the file header and last empty line
    data = [ ( float(data[0]), float(data[1]), float(data[2]) ) for data in (point.split(", ") for point in data) ]
        
    # Determine closest data point to ROC
    closest = 1000000  # Arbitrary large distance to start as closest
    precip = 0
    for point in data:
        a = (LATITUDE - point[1])
        b = (LONGITUDE - point[2])
        distance = sqrt(a**2 + b**2)

        if distance < closest:
            precip = point[0]
            closest = distance

    print(closest)

    # Save point to data.txt
    with open("data.txt", "w") as file:
        file.write(str(precip))
    

if __name__ == "__main__":
    main()