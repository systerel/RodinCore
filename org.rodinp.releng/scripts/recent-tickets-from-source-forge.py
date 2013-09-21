#!/usr/bin/env python3
#******************************************************************************
# Copyright (c) 2013 Systerel and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     Systerel - initial API and implementation
#******************************************************************************
#
#  Extracts tickets that have been modified after a given date
#  from SourceForge.
#

import argparse
import datetime
import json
import urllib.parse
import urllib.request
import sys

URL = 'http://sourceforge.net/rest/p/rodin-b-sharp/{tracker}/search?{params}'


def main():
    args = parse_args(sys.argv)
    extract_tickets('bugs', args.date)
    extract_tickets('feature-requests', args.date)


def parse_args(args):
    """Parse script arguments."""
    parser = argparse.ArgumentParser(
        description='''Extract closed tickets more recent than a given date
            from SourceForge.''')
    parser.add_argument('date', help='starting date (YYYY-MM-DD)', type=date)
    return parser.parse_args()


def date(string):
    """Check for date validity."""
    return datetime.datetime.strptime(string, '%Y-%m-%d')


def extract_tickets(tracker, start_date):
    """Extract tickets more recent than start_date from tracker."""
    query = 'status:closed* AND mod_date_dt:[{}Z TO *]'.format(
        start_date.isoformat())
    params = urllib.parse.urlencode({'q': query})
    url = URL.format(tracker=tracker, params=params)
    with urllib.request.urlopen(url) as response:
        contents = json.loads(response.read().decode('utf-8'))
    tickets = contents['tickets']
    print(title(tracker))
    for t in sorted(tickets, key=lambda t: t['ticket_num']):
        print(" {} {}".format(t['ticket_num'], t['summary'].strip()))
    print()


def title(tracker):
    """Title to print for the given tracker."""
    return ' '.join(map(lambda s: s.capitalize(), tracker.split('-')))

if __name__ == '__main__':
    main()
