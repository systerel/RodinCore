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
# Extract prover rule tags from Java source files.
#

use warnings;
use strict;
use integer;
use File::Find;

use constant DEBUG => 0;

# Rule names found are keys of this map
my %rules;

# Main program
find(\&filter, @ARGV);
print join("\n", sort keys %rules);
exit 0;

# Filter for find
sub filter {
	m/\.(?:java|t)$/ or return;
	processSourceFile($_);
}

# Process one Java source file
sub processSourceFile {
	my($filename) = @_;
	if ($filename eq 'ProverRule.java') {
		return; # avoid examples in interface source file
	}
	DEBUG and print STDERR "Processing $filename\n";
	open(FH, '<:utf8', $filename) or die $!;
	local $/; # enable localized slurp mode
	my $text = <FH>;
	while ($text =~ /@ \s* ProverRule \s* \( ([^)]*) \)/gx) {
		processRuleNames($1);
	}
	close FH;
	
}

# Process a list of rule names
sub processRuleNames {
	my($list) = @_;
	DEBUG and print STDERR "  Found $list\n";
	while ($list =~ /" ([^"]*) "/gx) {
		$rules{$1} = 1;
	}
}
