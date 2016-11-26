# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import datetime
import requests
import time
from collections import deque
from lxml import etree, html
from urllib.request import urlopen


site = 'https://myanimelist.net'

# write the name of the user profile
user = 'user'

# check lists on types. values: True = check, False = uncheck
flag_manga = True
flag_anime = True

# check on deep. values: True = check, False = uncheck
flag_deep = True


class Task(object):

    def __init__(self, link):
        self.link = link


def log_time():
    return datetime.datetime.now().strftime('%H:%M:%S')


def get_links(list_type, pass_str):

    queue = deque()
    initial_list = list()
    visited_set = set()
    resulted_set = set()

    # parse xml document
    tree = etree.parse(urlopen('{0}/malappinfo.php?u={1}&status=all&type={2}'.format(site, user, list_type)))
    root_xml = tree.getroot()

    print(u'{0} - get ids of {1}list links.'.format(log_time(), list_type))

    # add links from xml to queue
    for item in root_xml.iter('series_{0}db_id'.format(list_type)):
        initial_list.append('{0}/{1}/{2}/'.format(site, list_type, item.text))
        queue.append(Task('{0}/{1}/{2}/'.format(site, list_type, item.text)))

    print('{0} - added {1} links to initial_list of {2}.'.format(log_time(), len(initial_list), list_type))

    while queue:

        task = queue.popleft()

        if task.link in visited_set:
            continue

        visited_set.add(task.link)
        print('{0} - get page \"{1}\".'.format(log_time(), task.link))
        buffer_page = requests.get(task.link)

        # error 404 (not found)
        if buffer_page.status_code == 404:
            print('{0} - page \"{1}\" not found.'.format(log_time(), task.link))
            continue

        # error 429 (too many requests)
        while buffer_page.status_code == 429:
            print('{0} - page \"{1}\" not returned. Repeat GET request.'.format(log_time(), task.link))
            time.sleep(5)
            buffer_page = requests.get(task.link)

        # parse html document
        root_html = html.fromstring(buffer_page.content)

        for attr in root_html.xpath('//table[@class="anime_detail_related_anime"]//a/@href'):

            if ('{0}'.format(attr) != '/anime//') and ('{0}'.format(attr) != '/manga//'):
                buffer_link = '{0}/{1}/{2}/'.format(site, list_type, attr.split('/')[-2])
                print('{0} - check link \"{1}\" to add.'.format(log_time(), buffer_link))

                if (
                       (pass_str not in buffer_link) and
                       (buffer_link not in initial_list) and
                       (buffer_link not in resulted_set)
                ):
                    resulted_set.add(buffer_link)

                    print('{0} - link \"{1}\" added to the resulted_set.'.format(log_time(), buffer_link))

                    if flag_deep:
                        queue.append(Task(buffer_link))

                else:
                    print('{0} - link \"{1}\" not added to the resulted_set.'.format(log_time(), buffer_link))

    return resulted_set


with open('result.txt', 'wb') as file:

    if flag_manga:
        file.write('MANGALIST:\n'.encode('utf8'))
        file.write('\n'.join(get_links('manga', '{0}/anime/'.format(site))).encode('utf8'))
        file.write('\n'.encode('utf8'))

    if flag_anime:
        file.write('ANIMELIST:\n'.encode('utf8'))
        file.write('\n'.join(get_links('anime', '{0}/manga/'.format(site))).encode('utf8'))
        file.write('\n'.encode('utf8'))
