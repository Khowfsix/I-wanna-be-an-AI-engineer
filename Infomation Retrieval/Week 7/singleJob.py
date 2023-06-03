import bs4 as bs
import urllib.request

source = urllib.request.urlopen('https://www.vietnamworks.com/it-recruiter-102-1-1606933-jv/?source=searchResults&searchType=2&placement=1606934&sortBy=date').read()

soup = bs.BeautifulSoup(source, 'lxml')

jobName = soup.find("h1", class_="job-title").text.strip()
print("Job Name:", jobName)


description = soup.find("div", class_="description").text.strip()
print("Description: \n", description)

expiry_gray_light = soup.find("span", class_="expiry gray-light").text.strip()
print(expiry_gray_light)

benefits = soup.find("div", class_="what-we-offer mobile-box").findChild("h2")
print(benefits)


