from urllib.request import urlopen
from bs4 import BeautifulSoup
# import json
import re

dataList = []
html = urlopen(
    "http://www.cc.ntut.edu.tw/~wwwoaa/oaa-nwww/oaa-cal/oaa-cal_105.html")
soup = BeautifulSoup(html.read(), "html.parser")

# 所有<P>的清單
infoList = soup.findAll({'p'})

for info in infoList:
    # 尋找包含日期的資料
    for data in re.findall("\(\d*/\d*.*(?=<)|\(\d+/*\d*-*\d+/*\d*\).*(?=<)",
                           str(info)):
        try:
            # 將日期前的'('取代成' ('
            # 然後用' ('分割開來加入 dataList
            dataList += re.split("、\s*(?=\()|(?<=\S)\s+(?=\()",
                                 re.sub("(?<=\S)\((?=\d*/\d*\))", " (", data))
        except:
            pass
#print(dataList)

# 從所有清單中尋找當前學年度，並寫入 semester
for lookFor in dataList:
    if re.search("\d*(?=學年度第.學期開始)", lookFor):
        semester = int(re.search("\d*(?=學年度第.學期開始)", lookFor).group())

# 建立JSON格式
tmp = r'{"semester":"%s","eventList":[' % (semester)

# 找出開始與結束日期
for data in dataList:
    startDate = re.search("(\d+/*\d+)", data)
    endDate = re.search("(?<=\-)\d*/\d*", data)

    # 如果日期是3個數字連一起EX.810，將它分開 (行政人員輸入問題)
    # 然後將startDate轉型成str
    if re.match("\d{3,4}", startDate.group()):
        startDate = str(int(startDate.group()) // 100) + "/" + str(
            int(startDate.group()) % 100)
    else:
        startDate = startDate.group()

    # 有endDate的話
    if endDate is not None:

        # 如果日期是3個數字連一起EX.810，將它分開 (行政人員輸入問題)
        # 然後將endDate轉型成str
        if re.match("\d{3,4}", endDate.group()):
            endDate = str(int(endDate.group()) // 100) + "/" + str(
                int(endDate.group()) % 100)
        else:
            endDate = endDate.group()

    # 找出內文的部分
    event = re.search("\d*[\u4e00-\u9fa5]+.*", data)

    # 依照開始月份判斷是學期開始當年OR隔年
    if re.match(r"[0-7]/(?=\d*)", startDate):
        semester_end = semester_start = int(semester) + 1912
        flag = 0
    else:
        semester_end = semester_start = int(semester) + 1911
        flag = 1

    # 沒有結束日期的話直接寫入JSON
    if endDate is None:
        tmp += '{"startDate":"%s/%s","endDate":"%s/%s","event": "%s"},' % (
            semester_start, startDate, semester_start, startDate,
            event.group())
    # 有結束日期的話，判斷一下在寫入JSON
    else:
        # 如果是在1~7月且開始月份不再1~7月，年份+1
        if re.match(r"[0-7]/(?=\d*)", endDate) and flag:
            semester_end += 1
        tmp += '{"startDate": "%s/%s","endDate":"%s/%s","event":"%s"},' % (
            semester_start, startDate, semester_end, endDate, event.group())

# 補完JSON檔
tmp = tmp.rstrip(',')
tmp += ']}'
tmp = tmp.replace('/', '-')

# 輸出
with open('calendar.json', 'w', encoding='utf8') as f:
    f.write(tmp)
#print(tmp)
