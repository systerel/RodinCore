#!/usr/bin/env python3
# vim: set fileencoding=utf-8
"""Check copyright notices in Java sources for Rodin.

Verify that notices are well-formed with respect to the Rodin coding
guidelines.

@author: Laurent Voisin
"""

import argparse
import re
import sys

# Maximal number of bytes in file header
MAX_HEADER_SIZE = 2000

FIRST_LINE = '/' + '*' * 79
YEAR = '(20[0-9][0-9])'
HOLDER = (
        '('
        + '|'.join([re.escape(name) for name in (
                'IBM Corporation',
                'ETH Zurich',
                'Systerel',
                'UFRN',
                'University of Southampton',
                'Universitaet Duesseldorf',
                'Sebastian Davids',
                'Igor Fedorenko',
        )])
        + ')')
COPYRIGHT_LINE = re.compile(
        re.escape(' * Copyright (c) ')
        + YEAR
        + '(?:, ' + YEAR + ')? '
        + HOLDER
        + re.escape(' and others.\n'),
        re.MULTILINE)
MIDDLE_LINES = (
    ' * All rights reserved. This program and the accompanying materials',
    ' * are made available under the terms of the Eclipse Public License v1.0',
    ' * which accompanies this distribution, and is available at',
    ' * http://www.eclipse.org/legal/epl-v10.html',
    ' *',
    ' * Contributors:',
)
CONTRIBUTION = re.compile(
        re.escape(' *     ')
        + HOLDER
        + re.escape(' - ')
        + '([^ \t].*[^ \t])\n',
        re.MULTILINE)
INIT_API = 'initial API and implementation'
LAST_LINE = ' ' + '*' * 79 + '/'
PACKAGE_LINE = re.compile('^package')

def main(ns):
    """Main program"""
    errors = [ process(file, ns.verbose) for file in ns.files]
    errors = [ f + '\n  ' + e for (f,e) in zip(ns.files, errors) if e]
    if errors:
        print('\n'.join(errors))
    sys.exit(int(errors != []))

def parse_args():
    """Parse command line arguments"""
    argparser = argparse.ArgumentParser(
            description=
            'Check copyright notices in Java sources of the Rodin platform.')
    argparser.add_argument('-v', '--verbose', action='store_true',
            help='print the name of each file as it is processed')
    argparser.add_argument('files', metavar='FILE', nargs='+',
            help='Java source file to check')
    return argparser.parse_args()

def process(file, verbose=False):
    """Check a Java source file"""
    with open(file, 'r') as f:
        lines = f.readlines(MAX_HEADER_SIZE)
        if verbose:
            print('***', file)
            print(' ', '  '.join(lines))
        error = check(file, lines, verbose)
        return error

def check(file, lines, verbose):
    """Check the header of the file"""

    def invalid_line(idx):
        return 'Invalid line #' + str(idx+1) + ': ' + lines[idx]

    if not match_fixed(lines[0], FIRST_LINE):
        return invalid_line(0)
    m = match_copyright_line(lines[1])
    # Check that copyright years are in right order
    if not m or (m[1] and m[0] >= m[1]):
        return invalid_line(1)
    main_holder = m[2]
    for idx in range(0, len(MIDDLE_LINES)):
        if not match_fixed(lines[idx+2], MIDDLE_LINES[idx]):
            return invalid_line(idx+2)
    idx = 2 + len(MIDDLE_LINES)
    m = match_contribution(lines[idx])
    if not m or m.group(1) != main_holder or m.group(2) != INIT_API:
        return invalid_line(idx)
    idx += 1
    while True:
        m = match_contribution(lines[idx])
        if not m:
            break
        # First contributor occurs only once
        if m.group(1) == main_holder:
            return invalid_line(idx)
        idx += 1
    if not match_fixed(lines[idx], LAST_LINE):
        return invalid_line(idx)
    idx += 1
    if not match_package_line(lines[idx]):
        return invalid_line(idx)
    return None

def match_fixed(line, pattern):
    """Tells whether the line matches a fixed pattern"""
    return line == pattern + '\n'

def match_copyright_line(line):
    """Tells whether the line matches a copyright line"""
    m = COPYRIGHT_LINE.match(line)
    return m and m.groups()

def match_contribution(line):
    """Tells whether the line matches a contribution"""
    return CONTRIBUTION.match(line)

def match_package_line(line):
    """Tells whether the line matches a package declaration"""
    return PACKAGE_LINE.match(line)

if __name__ == '__main__':
    ns = parse_args()
    main(ns)

# vim: sw=4 sts=4 ts=4 et ai
