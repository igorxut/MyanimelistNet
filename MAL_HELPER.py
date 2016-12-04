# !/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import time
from collections import deque
from datetime import datetime
from lxml import etree, html
from urllib.request import urlopen


def write_log(f_file, f_text):

    with open(f_file, "ab") as f:
        f.write(f_text.encode("utf8"))
        f.write("\n".encode("utf8"))


def get_list(f_list, f_user, f_site, f_file):

    for item_type in (
            "anime",
            "manga",
    ):

        link = "{0}/malappinfo.php?u={1}&status=all&type={2}".format(
            f_site,
            f_user,
            item_type,
        )
        tree = etree.parse(urlopen(link))
        root_xml = tree.getroot()
        log_text = "{0}\tparsing of xml from url \"{1}\".".format(
            datetime.now().time(),
            link,
        )
        print(log_text,)

        for item in root_xml.iter("series_{0}db_id".format(item_type,)):

            try:
                item_id = int(item.text)

            except ValueError:
                item_id = item.text
                log_text = "{0}\t{1}_id={2} is not int.\tValueError".format(
                    datetime.now().time(),
                    item_type,
                    item_id,
                )
                write_log(f_file, log_text)

            f_list.append((item_type, item_id,))
            log_text = "{0}\tadded {1}_id={2} to list.".format(
                datetime.now().time(),
                item_type,
                item_id,
            )
            print(log_text,)

        log_text = "{0}\tparsing of {1} list is complete.".format(
            datetime.now().time(),
            item_type,
        )
        print(log_text,)

    log_text = "{0}\tgetting list is complete.".format(
        datetime.now().time(),
    )
    print(log_text,)

    return f_list


def get_page(f_site, f_task, f_file):

    link = "{0}/{1}/{2}/".format(
        f_site,
        f_task[0],
        f_task[1],
    )
    page = requests.get(
        link
    )

    # error 404 (not found)
    if page.status_code == 404:
        log_text = "{0}\tpage \"{1}\" not found.\tpage not found".format(
            datetime.now().time(),
            link,
        )
        print(
            log_text,
        )
        write_log(
            f_file,
            log_text
        )
        return None

    # error 429 (too many requests)
    counter = 0
    while page.status_code == 429:

        if counter == 9:
            log_text = "{0}\tpage \"{1}\" not returned 10 times.\tpage not returned".format(
                datetime.now().time(),
                link,
            )
            write_log(
                f_file,
                log_text
            )
            break

        counter += 1
        log_text = "{0}\tpage \"{1}\" not returned. Repeat GET request in 5 seconds.".format(
            datetime.now().time(),
            link,
        )
        print(
            log_text,
        )
        time.sleep(
            5
        )
        page = requests.get(
            link
        )

    return page


def parse_page(
        f_page,
        f_file
):

    root_html = html.fromstring(
        f_page.content
    )
    f_set = set()

    for attr in root_html.xpath(
            '//table[@class="anime_detail_related_anime"]//a/@href'
    ):

        f_list = attr.split(
            '/'
        )
        item_type = f_list[1]

        try:
            item_id = int(
                f_list[2]
            )

        except ValueError:

            if f_list[2] == "":
                continue

            item_id = f_list[2]
            log_text = "{0}\t{1}_id={2} is not int.\tValueError".format(
                datetime.now().time(),
                item_type,
                item_id,
            )
            write_log(
                f_file,
                log_text
            )

        task = (
            item_type,
            item_id,
        )
        f_set.add(
            task
        )

    return f_set


def main(user):

    site = "https://myanimelist.net"

    user_list = list()
    visited_set = set()
    queue = deque()

    file_result = "result.tsv"
    file_error_log = "errors.tsv"

    result_file = open(
        file_result,
        "wb"
    )
    result_file.close()
    error_log_file = open(
        file_error_log,
        "wb"
    )
    error_log_file.close()

    user_list.extend(
        get_list(
            user_list,
            user,
            site,
            file_error_log
        )
    )
    queue.extend(
        user_list
    )

    while queue:

        task = queue.popleft()

        if task in visited_set:
            continue

        if task not in user_list:
            log_text = "{0}\t{1} added to result_set.".format(
                datetime.now().time(),
                task,
            )
            print(
                log_text
            )
            log_text = "{0}\t{1}\t{2}/{0}/{1}/".format(
                task[0],
                task[1],
                site,
            )
            write_log(
                file_result,
                log_text
            )

        visited_set.add(task)
        log_text = "{0}\t{1} added to visited_set.".format(
            datetime.now().time(),
            task,
        )
        print(
            log_text,
        )

        page = get_page(
            site,
            task,
            file_error_log
        )

        if page is None:
            continue

        else:
            urls_set = parse_page(
                page,
                file_error_log
            )

        for item in urls_set:

            if item not in visited_set:
                queue.append(
                    item
                )
                log_text = "{0}\t{1} added to queue.".format(
                    datetime.now().time(),
                    item,
                )
                print(
                    log_text
                )

    log_text = "{0}\tqueue is empty, mission complete.".format(
        datetime.now().time()
    )
    print(log_text)

    return None

if __name__ == "__main__":
    main("XuT")
