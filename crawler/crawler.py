from urllib.request import urlopen
from bs4 import BeautifulSoup
import json
import re

html = urlopen("http://www.cc.ntut.edu.tw/~wwwoaa/oaa-nwww/oaa-cal/oaa-cal_105.html")
soup = BeautifulSoup(html.read(), "html.parser")
infoList = soup.findAll({'p'})
dataList = []
for info in infoList:
    for data in re.findall("\(\d*/\d*.*(?=<)|\(\d+/*\d*-*\d+/*\d*\).*(?=<)",str(info)):
        if data:
            dataList += re.split("、\s*(?=\()|(?<=\S)\s+(?=\()",re.sub("(?<=\S)\((?=\d*/\d*\))" , " (" ,  data))
print(dataList)
for lookFor in dataList:
    if re.search("\d*(?=學年度第.學期開始)",lookFor):
        semester = int(re.search("\d*(?=學年度第.學期開始)",lookFor).group())
tmp =r'{"semester":"%s","eventList":[' %(semester)
for data in dataList:  
    startDate = re.search("(\d+/*\d+)", data)
    endDate = re.search("\-\d*/\d*", data)
    if re.match("\d{3,4}",startDate.group()):
        startDate = str(int(startDate.group())//100)+"/"+str(int(startDate.group())%100)
    else:
        startDate = startDate.group();
    if endDate != None:
        if re.match("\d{3,4}",endDate.group()):
            endDate = str(int(endDate.group())//100)+"/"+str(int(endDate.group())%100)
        else:
            endDate = endDate.group()
    event = re.search("\d*[\u4e00-\u9fa5]+.*", data)
    if re.match(r"[0-7]/(?=d*)", startDate):
        semester_start = int(semester) + 1912
    else:
        semester_start = int(semester) + 1911
        semester_end = int(semester) + 1911
    if endDate == None:
        
        tmp += '{"startDate":"%s/%s","endDate":"%s/%s","event": "%s"},' % (semester_start, startDate, semester_start, startDate, event.group())
    else: 
        if re.match(r"[0-7]/(?=d*)", endDate):
            semester_end = int(semester) + 1912
        endDate = re.sub("-", "", endDate)
        tmp += '{"startDate": "%s/%s","endDate":"%s/%s","event":"%s"},' % (semester_start, startDate, semester_end, endDate, event.group())
tmp = tmp.rstrip(',')
tmp += ']}'
tmp = tmp.replace('/', '-')
with open('calendar.json', 'w', encoding='utf8') as f:
    f.write(tmp)
print (tmp) 