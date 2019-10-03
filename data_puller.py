from lxml import html
import requests
from datetime import timedelta, date


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
    
    lines = [", ".join(line.strip().split()) \
        for line in precip_data][6:len(precip_data) - 1] # [6:last] to get rid of the file header and last empty line
        
    print(lines)

    # doc = html.fromstring(page.content)
    # xpath_precip = '/html/body/app/city-history/city-history-layout/div/div[2]/section/div[2]/div[2]/div/div[1]/div/div/city-history-summary/div/div[2]/table/tbody[2]/tr/td[1]'
    # print(doc.xpath(xpath_precip))
    

if __name__ == "__main__":
    main()