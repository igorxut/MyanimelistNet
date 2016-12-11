import requests
from lxml import html

from fake_useragent import UserAgent
#
# class Proxy(object):
#     proxy_url = "http://hideme.ru/proxy-list/"
#     proxy_list = list()
#
#     def __init__(self):
#         page = requests.get(self.proxy_url)
#         root_html = html.fromstring(page.content)
#         result = root_html.xpath('//table[@class="proxy__t"]//tbody//tr/td'):


# proxy_url = "http://hideme.ru/proxy-list/"
# proxy_list = list()
# page = requests.get(proxy_url)
# root_html = html.fromstring(page.content)
#
# for tr in root_html.xpath('//table[@class="proxy__t"]//tbody//tr'):
#     proxy_list.append("{0}:{1}".format(tr.xpath('td[1]/text()')[0], tr.xpath('td[2]/text()')[0]))
#
# print(proxy_list)