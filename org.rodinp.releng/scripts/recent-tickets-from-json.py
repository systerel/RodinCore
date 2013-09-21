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
#  from a SourceForge backup in JSON format.
#

import argparse
import datetime
import json
import sys


def parse_args(args):
    """Parse script arguments."""
    parser = argparse.ArgumentParser(
        description='''Extract closed tickets more recents that a given date
            from a SourceForge backup.''')
    parser.add_argument('date', help='starting date (YYYY-MM-DD)', type=date)
    parser.add_argument('file', help='file in JSON format')
    return parser.parse_args()


def date(string):
    """Check for date validity."""
    return datetime.datetime.strptime(string, '%Y-%m-%d')


def main():
    args = parse_args(sys.argv)
    start_date_string = args.date.isoformat()
    with open(args.file) as file:
        contents = json.load(file)

    def interesting(ticket):
        """Criteria for filtering tickets to show."""
        return (ticket['mod_date'] > start_date_string
                and ticket['status'].startswith('close'))

    tickets = filter(interesting, contents['tickets'])
    for t in sorted(tickets, key=lambda t: t['ticket_num']):
        print(" {} {}".format(t['ticket_num'], t['summary'].strip()))

if __name__ == '__main__':
    main()
