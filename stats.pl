#!/usr/bin/perl
use strict;
use warnings;
use v5.10.0;
use JSON::PP;
use LWP::UserAgent;

my $url = shift // 'http://localhost:8080/metrics';
my $ua = LWP::UserAgent->new();
my $res = $ua->get($url);
my $dat = decode_json($res->content);

my ($success, $fail) = success_fail();

printf "average elapsed time: %f\n", 1.0*total_elapsed() / ($success+$fail);
printf "response status: success:%d, fail:%d\n", $success, $fail;

sub total_elapsed {
    my $ret = 0;
     for my $key (grep /\Agauge\.response\./, keys %$dat) {
        $ret += $dat->{$key};
    }
    return $ret;
}

sub success_fail {
    my $success = 0;
    my $fail = 0;
     for my $key (keys %$dat) {
         if ($key =~ /\Acounter\.status\.([0-9])/) {
             if ($1==2 || $1==3) {
                $success += $dat->{$key};
             } else {
                $fail += $dat->{$key};
             }
         }
    }
    return ($success, $fail);
}
