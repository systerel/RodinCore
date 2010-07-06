#!/usr/bin/perl
################################################################################
# Copyright (c) 2009 Systerel and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     Systerel - initial API and implementation
################################################################################
#
# Extract prover rule names from Event-B wiki
# Only rules that are marked as implemented are extracted.
#

use warnings;
use strict;
use integer;
use LWP::Simple;

use constant DEBUG => 0;
use constant WIKI_EXPORT => "http://wiki.event-b.org/index.php/Special:Export";

# Rule names found are keys of this map
my %rules;

# Main program
extract(qw/Inference_Rules
       Set_Rewrite_Rules Relation_Rewrite_Rules Arithmetic_Rewrite_Rules/);
print join("\n", sort keys %rules);
exit 0;

# Extract a list of pages
sub extract {
	for my $page (@_) {
		extractPage($page);
	}
}

# Process one wiki page
sub extractPage {
	my($page) = @_;
	my $url = WIKI_EXPORT . "/$page";
	DEBUG and print STDERR "Processing $url\n";
	my $content = get($url);
	DEBUG and print STDERR "$content\n";
	while ($content =~ /\|\s*\S\s*\|\|\s*\{\{ \s* Rulename \s* \| \s* (\S+) \s* \}\}/gx) {
		DEBUG and print STDERR "  Found $1\n";
		$rules{$1} = 1;
	}
}

