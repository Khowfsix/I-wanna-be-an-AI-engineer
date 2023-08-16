#%%
import time
from selenium import webdriver
from selenium.webdriver.common.by import By

#%%
# get driver
driver = webdriver.Chrome()

#%%
# get web
driver.get("https://www.vietnamworks.com/it-kv")
time.sleep(3)
# driver.implicitly_wait(time_to_wait=0.5)

#%%
driver.execute_script("window.scrollTo(0, Y)")

#%%
# get total of job
no_of_jobs = driver.find_element(by=By.XPATH,value='//span[@class="no-of-jobs"]/strong').get_attribute("innerHTML")
no_of_jobs = int(no_of_jobs.strip())
print(no_of_jobs)

#%%
# link job
linkJob = []

#%%
# get list Job
jobs = driver.find_elements(By.XPATH, '//div[@class="col-12 col-lg-8 col-xl-8 p-0 wrap-new"]/a')

#%%
# get link of each job
for job in jobs:
    linkJob.append(job.get_attribute("href"))