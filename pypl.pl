#!/usr/bin/perl -w

use strict;


my @lines=<>;             # input lines
my $layer=0;              # a variable capture the indentaion layer
my $IND = "   ";          # a default indentation length for perl
my @out_lines = @lines;   # output lines to modify later
my %var;                  # a hash %var to capture the variable name and type

####
# get_index -- a function get the next index key of the token hash
# so as to get the proper index to modify the out_lines array when it reassemble
# $i : current index of lines
# %tk: the token to reassemble translation
####
sub get_index{
  my ($i, %tk) = @_;
  my @ps = sort{$a<=>$b} keys %tk;
  my $p = $i;
  $p = $ps[-1]+1 if ($#ps != -1 and $ps[-1]+1 > $i);
  return $p;
}

####
# proc_single_quote -- a function process the single quote string
# @_ arguement: a line of string
####
sub proc_single_quote{
  my ($val) = @_;
  # substitute the single quote with double quote
  # escape the double quote in the single quote
  $val =~ s/"/\"/g if ($val =~ m/^'.*'$/);
  $val = proc_var($val) if ($val =~ m/^'.*'$/);
  $val =~ s/^'/"/g;
  $val =~ s/'$/"/g;
  return $val;
}

###
# proc_var -- a function process variable inside the function or a loop
# statement or a line to convert the variable with proper sign based on
# the type of the variable
# @_ arguement: a line of string
###
sub proc_var{
  my ($var_val) = @_;

  # substitute the comment line arguements
  $var_val =~ s/sys.argv/ARGV/g;
  # get the variable name in the var hash
  my @vars = sort{length $b <=> length $a} keys %var;

  foreach my $k (@vars) {
    # translate all predefine variables based on the type
    # for different case
    if ($var_val !~ m/'.*'$/) {
      # there is variables when it is not inside the single quote
      # mark all the variable as $ if that is not in len function
      $var_val =~ s/\b$k\b/\$$k/g if ($var_val !~ m/len\([^"]/);
      # change $ as @ when it is in the pop function
      $var_val =~ s/\$\b$k\b\.pop\(.*\)/pop \@$k/g;
      # change $ as % when it used with keys() and the type is "h"
      $var_val =~ s/\$\b$k\b\.keys\(\)/keys %$k/g if ($var{$k} eq "h");
      # translate len() with length($ ) when variable is string/number
      $var_val =~ s/len\($k\)/length\(\$$k\)/g if ($var{$k} eq "p");
      # translate len() with scaler(@ ) when variable is array
      $var_val =~ s/len\($k\)/scalar\(\@$k\)/g if ($var{$k} eq "a");
      # translate sorted() with sort(@ ) when sort items is array
      $var_val =~ s/sorted\(\$$k\)/sort\(\@$k\)/g if ($var{$k} eq "a");
      # convert the dictionary square brackets as curly brackets
      $var_val =~ s/\b$k\b\[(.*)\]/$k\{$1\}/g if ($var{$k} eq "h");
      # update the variable with @ if the type is array
      $var_val =~ s/\$\b$k\b/\@$k/g if ($var{$k} eq "a");
      # update the variable with $ if it follows with square bracket
      $var_val =~ s/\@\b$k\b(\[.*?)/\$$k$1/g if ($var{$k} eq "a");
    }
  }
  # sort out single quote arguement in the assignment
  # translate sorted function with single quote to sort
  $var_val =~ s/sorted\(([^@]*)\)/sort\($1\)/g;
  # escape the double quote inside single quote
  $var_val =~ s/"/\\"/g if ($var_val =~ m/'.*'$/);
  # substitute the single quotes with double quotes
  # the open quote could be behind the equal sign
  $var_val =~ s/(= ?)'/$1"/g;
  $var_val =~ s/'$/"/g;
  # translate len funtion with string to length with string in perl
  $var_val =~ s/len\((".*?")\)/length\($1\)/g;
  $var_val =~ s/len\('(.*?)'\)/length\("$1"\)/g;
  return $var_val;
}

####
# lexe_analysis -- a function to break each line into individual token
# and perform the analysis for each token so as to reassemble in
# the parser
# @lines : the input lines
####
sub lexe_analysis{
  my (@lines) = @_;
  # tokens for translation reassemble
  my %tk;
  for (my $i=0; $i < @lines; $i++) {
    my $l = $lines[$i];
    chomp $l;
    # remove  comment mix with code
    $l =~ s/(.*[\w]);?\s*#.*$/$1/;
    # remove ; at then end
    $l =~ s/;$//g;

    if ($l =~ m/^(#!?.*|\s*)$/) { # skip the comment
    } elsif ($l =~ m/^(import).*/) {
      # skip the import
      my $keyword = $l;
      $keyword =~ s/^(import)(.*)/$1/g;
      $tk{get_index($i, %tk)}{$keyword}="";

    } elsif ($l =~ m/^(\s*)?(break|continue)/g) {

      #translate keywords break and continue
      my $keyword = $l;
      $keyword =~ s/^.*(break)/$1/g;
      $tk{get_index($i, %tk)}{$keyword}="";

    } elsif ($l =~ m/^(\s*)?[^=]*(pop\()/g) {

      my $val = $l;
      $val = proc_var($val);
      $tk{get_index($i, %tk)}{pop}=$val;

    } elsif ($l =~ m/^(\s*)?.*(append\()/g) {

      # translate append to push
      my $exp = $l;
      $exp =~ s/^(\s*)?(.*)(\.append\()(.*)\)/push \@$2, /g;

      # get the append value
      my $val = $l;
      $val =~ s/^(\s*)?(.*)(\.append\()(.*)\)/$4/g;
      # go through the proc_var function
      $val = proc_var($val);
      $tk{get_index($i, %tk)}{push}=$exp.$val;

    } elsif ($l =~ m/^(\s*)?(print|sys\.stdout\.write)/g) {

      # print/sys.stdout.write functions, first get the function name
      my $keyword = $l;
      $keyword =~ s/^.*(print|sys\.stdout\.write)\(.*\)/$1/g;
      # make assignment $l to $val to translate the print content
      my $val = $l;

      if ($l =~ m/^(\s*)?print\(.*, ?end=.*\)/) {

        # get the print content
        $val =~ s/^(\s*)?print\((.*), ?end=.*\)/$2/g;
        # get the end argument
        my $end = $l;
        $end =~ s/^(\s*)?print\((.*), ?end=(.*)\)/$3/g;
        # remove single quote
        $end =~ s/^'(.*)'$/$1/g;

        $val = proc_var($val);
        $val =~ s/"//g;
        $val =~ s/'//g;
        # attach the end argument to print content
        $val = "$val\n$end" if ($end ne "\"\"");
        # put quotation mark for perl print.
        $val = "\"$val\"";

      } elsif ($l =~ m/".*(%.*)[^\\]"/) {

        # when print output is string format
        # get the output string format

        $val =~ s/.*$keyword\((".*[^\\]") % (.*)/$1/g;

        # splite the output variables from tuple
        my $v = $l;
        $v =~ s/.*$keyword\((".*[^\\]") % ?(.*)\)/$2/g;
        $v =~ s/\((.*)\)/$1/g;
        # flatter out individual variable
        my @vs = split(/, /, $v);

        foreach my $v (@vs) {
          # when it is variable
          $v = proc_var($v) if ($v !~ m/^'.*'$/);
          # when it is string
          $v =~ s/"/\\"/g if ($v =~ m/^'.*'$/);
          $v =~ s/^'(.*)'$/$1/ if ($v =~ m/^'.*'$/);
        }

        # substitute the output variable in the string format
        foreach my $var_val (@vs) {
          $val =~ s/(%\w*)/$var_val/;
        }

        # add new line charactor if it is print function
        $val =~ s/(.*)"$/$1\\n"/g if ($keyword eq "print");

      } elsif ($l =~ m/$keyword\(("|').*[^\\]("|')\)/) {

        # when output is string, get the string
        $val =~ s/\s*$keyword\((.*)\)$/$1/g;
        $val = proc_single_quote($val);
        # add new line charactor if it is print function
        $val =~ s/(.*)"$/$1\\n"/g if ($keyword eq "print");

      } elsif ($l =~ m/\(.+\)$/) {

        # when output is variable; get printing variables
        $val =~ s/\s*$keyword\((.*)\)$/$1/g;

        $val = proc_var($val);
        # attached new line charactor because print($answer0 * $answer1)
        $val = "$val\.\"\\n\"" if ($keyword eq "print" and $val !~ /.*, ?.*/);


        # substitute comma with space
        $val =~ s/"//g if ($val =~ /.*, ?.*/);
        $val =~ s/'//g if ($val =~ /.*, ?.*/);
        $val =~ s/, /,/g if ($val =~ /.*, ?.*/);
        $val = "\"$val\\n\"" if ($val =~ /.*, ?.*/);
        $val =~ s/,/ /g if ($val =~ /.*, ?.*/);

        # put quotation mark for perl print if it is contain comma.


      } else {
        # when output is nothing
        $val =~ s/\s*print\((.*)\)$/$1"\\n"/g;
      }
      # take sys.stdout.write as print
      $tk{get_index($i, %tk)}{print}=$val;

    } elsif ($l =~ m/^(\s*)?(while|if|elif|else|for)/g) {
      # process while if elif else for loop block
      my @vars = keys %var;
      my $loop = $l;
      my $exp = $l;
      my $con = $l;

      $exp =~ s/ not ?/ !/g;

      # update current layer for indentation at the beginning of loop
      $layer++;
      if ($l =~ m/^(\s*)?for .*:.*/) {
        # for loop block

        # translate for to foreach in perl
        $con =~ s/^(\s*)?(for).*/$2each/g;
        my $var_name = $l;
        $var_name =~ s/^(\s*)?for (\w*) in .*/$2/g;
        # make the for loop block variable at premitive
        $var{$var_name}='p';
        @vars = keys %var;

        if ($l =~ m/in range\(/) {
          my $end = $l;

          if ($l =~ m/^(\s*)?for (\w*) in range\(([^,]*)\)/) {
            # for range has 1 arguements

            # get the variable and set start number as 0
            $exp =~ s/^(\s*)?for (\w*) in range\(([^,]*)\):/ \$$2 (0../g;
            # get the number in range as the end number
            $end =~ s/^(\s*)?for (\w*) in range\(([^,]*)\):/$3/g;

          } elsif ($l =~ m/^(\s*)?for (\w*) in range(.*), (.*)/) {
            # for loop with range(0, 5) has 2 arguments

            # for range has 2 arguements
            # get the variable and the start number
            $exp =~ s/^(\s*)?for (\w*) in range(.*), (.*)/ \$$2 $3../g;
            # get the end number
            $end =~ s/^(\s*)?(for)(.* in range)(.*), (.*)\):.*/$5/g;
          }

          if ($end !~ m/^(-)?\d*$/) {
            # process end value when it is number
            foreach my $k (@vars) {
              $end =~ s/$k/\$$k/g;
            }
            $end = "($end)-1"
          } else {
            # subtract 1 from end number
            $end--;
          }

          # conpose the for loop for perl
          $exp = "$exp$end) {";

        } elsif ($l =~ m/.* in sys\.stdin.*/) {
          # handle for loop witn sys.stdin
          $exp =~ s/^(\s*)?for (\w*) in sys\.stdin.*/\$$2 (<STDIN>) {/g;

        } else {

          # handle for key in  sorted(a.keys()) loop
          # get the content after in
          my $in_scope = $l;
          $in_scope =~ s/^(\s*)?for (\w*) in ?(.*) ?:(.*)/$3/g;
          $in_scope =proc_var($in_scope);

          # conpose the for loop for perl
          $exp =~ s/^(\s*)?for (\w*) in(.*):(.*)/\$$2 ($in_scope) {/g;
        }
        # single line loop; split the loop body to expand later
        $loop =~ s/^(\s*)?(for)(.* in .*):(.*)/$4/g;

      }
      if ($l =~ m/(else):.*/) {
        # else block

        $exp = " {";
        $con = "else";

        # single line loop; split the loop body to expand later
        $loop =~ s/^(\s*)(else):(.*)/$3/g;

      }
      if ($l =~ m/(while|if|elif) (.*):.*/) {
        # get while condition expression
        $con =~ s/(\s*)?(while|if|elif) (.*)?:.*/$2/g;
        $exp =~ s/(\s*)?(while|if|elif) ([^:]*)?:.*/\($3\) {/g;
        $exp = proc_var($exp);
        # single line loop; split the loop body to expand later
        $loop =~ s/^(\s*)(while|if|elif) .*?:(.*)/$3/g;
      }

      # put in the translation token hash
      $tk{get_index($i, %tk)}{$con}=$exp;

      if ($loop ne "") { # if the loop is not empty
        # expend the 1 line body as a loop block

        # split the body by ;
        my @sublines = split(/;/, $loop);

        # get tokens hash from lexe_analysis for the loop block
        my %sub=lexe_analysis(@sublines);
        my @ks = sort{$a<=>$b} keys %sub;

        # merge the expaned lines into existing token
        my @ps = sort{$a<=>$b} keys %tk;
        foreach my $k (@ks) {
          foreach my $v (keys %{$sub{$k}}) {
            foreach my $j (@vars) {
              $sub{$k}{$v} =~ s/^[\$]?($j)/\$$1/g;
            }
            $tk{get_index($i, %tk)}{$v}=$sub{$k}{$v};
          }
        }
        # update current layer for indentation at the end of loop
        $layer--;
        $tk{get_index($i, %tk)}{$con}="}";
      } else {

        # get the indentation space for the loop block
        my $in_space = $lines[$i+1];
        chomp $in_space;
        $in_space =~ s/^(\s*)(.*)/$1/g;

        #multiple lines loop
        # get all indented lines match this level
        my @loop_block;
        my $j = $i;
        for( $j++; $j <= $#lines; $j ++) {
          my $line = $lines[$j];

          if ($line =~ m/^$in_space/) {
            push @loop_block, $line;
          } else {
            $j--;
            # stop once the indentation is not match this level
            last;
          }
        }

        # get tokens hash from lexe_analysis for the loop block
        my %sub=lexe_analysis(@loop_block);
        my @ks = sort{$a<=>$b} keys %sub ;

        # merge the expaned lines with existing token
        foreach my $k (@ks) {
          foreach my $v (keys %{$sub{$k}}) {
            foreach my $j (@vars) {
              $sub{$k}{$v} =~ s/^[\$]?($j)/\$$1/g;
            }
            $tk{get_index($i, %tk)}{$v}=$sub{$k}{$v};
          }
        }

        $tk{get_index($i, %tk)}{$con}="}";

        # update current layer for indentation at the end of loop
        $layer--;
        $i=$j;
      }

    } elsif($l =~ m/^(\s*)?((\w*\[?.*\]?) ?=( )?\W*)/) {
      # translate variable and its assignment

      # get the variable name
      my $var_name= $l;
      $var_name =~ s/^(\s*)?(\w*(\[['\w\d]*\])?) ?([-\*\/=\+] ?.*)/$2/g;

      # mark the type of variable as premitive
      $var{$var_name}="p" if (!exists $var{$var_name});
      $var{$var_name} = "p" if ($var_name =~ m/^\d*$/);
      $var{$var_name} = "p" if ($var_name =~ m/^['"].*["']$/);

      # get the variable value
      my $var_val = $l;
      # make the // as /
      $var_val =~ s/\/\//\//g;
      # get rid of things before = sign
      $var_val =~ s/^(\s*)?(\w*(\[[\w\d']*\])?)//g;
      $var{$var_name} = "p" if ($var_val =~ m/pop\(.*\)/);

      # convert dictionary a['key'] as perl form $a{'key'}
      # get the name of the array/hash
      my $vn = $var_name;
      $vn =~ s/\[.*//g;
      # check if the name in the %var is "h"-> hash;
      # change square bracketsas curly bracket
      $var_name =~ s/\[(.*)\]/\{$1\}/g if ($var{$vn} eq "h");

      # let variable value handle
      #  arithmetic operators: + - * / // % **

      if ($l =~ m/.*=(.*)?sys.stdin.readline(s)?\(\)/g) {
        # handle sys.stdin.readline(s) function return assignment

        # update this variable name as "a" -> array when it readlines
        $var{$var_name}="a" if ($l =~ m/\blines\b/);
        # convert it as perl <STDIN>
        $var_val =~ s/(.*=)(.*sys.stdin.readline(s)?\(\).*)/$1 <STDIN>/g;
        # put into tk hash with proper type according readline(s)
        $tk{get_index($i, %tk)}{"\@$var_name"}=$var_val if ($l =~ m/lines\(/);
        $tk{get_index($i, %tk)}{"\$$var_name"}=$var_val if ($l =~ m/line\(/);

      } elsif ($l =~ m/.*=(.*)?sorted\(.*\)/g) {
        # handle sorted() return assignment

        # update this variable as array
        $var{$var_name}="a";

        $var_val = proc_var($var_val);
        # put array variable name with @ sign into tokens hash
        $tk{get_index($i, %tk)}{"\@$var_name"}=$var_val;

      } elsif ($var_val =~ m/=? \{.*\}/) {
        # handle dictionary assignment

        # split square bracketsfrom dictionary variable name
        $var_name =~ s/(.*)\[(.*)\]/$1\{$2\}/;

        # update this variable name as "h" -> hash
        $var{$var_name}="h";

        # translate python dictionary to perl hash
        # replace { } with ( )
        $var_val =~ s/(=? )\{(.*)\}/$1\($2\)/;
        # replace : with =>
        $var_val =~ s/:/=>/g;
        # replace sign quote in key with double quote
        $var_val =~ s/'/"/g;
        $tk{get_index($i, %tk)}{"%$var_name"}=$var_val;

      } elsif ($var_val =~ m/\[(.*,?)*\]/) {
        # handle array assignment

        # update this variable name as "a" -> array
        $var{$var_name}="a";
        # replace [ ] with ( )
        $var_val =~ s/(=? ?\W)\[(.*)\]/$1\($2\)/;

        $tk{get_index($i, %tk)}{"\@$var_name"}=$var_val;

      } else {

        $var_val = proc_var($var_val);
        $tk{get_index($i, %tk)}{"\$$var_name"}=$var_val;
      }

    } elsif ($l =~ /.*len\(/) {
      # handle len function
      my $val = $l;
      if ($l =~ m/("|').*[^\\]("|')/) {
        # when argument is string

        # strip the content
        $val =~ s/\s*len\((.*)\)$/$1/g;

        $val = proc_single_quote($val);
        $val = "length($val)";

      } elsif ($l =~ m/\(.+\)$/) {
        # when argument is variable

        # get the variable and process the translation in proc_var
        $val = proc_var($val);
      }

      $tk{get_index($i, %tk)}{len}=$val;
    }
  }
  return %tk;
}
####
# parser -- a function to reassemble the tokens from lexe_analysis
# %res: the tokens has been processed and ready to reassemble
####
sub parser{
  my (%res) = @_;
  # reset the indentation layer
  $layer=0;
  # replace the hash bang for perl
  $out_lines[0] =~ s/(\/bin\/.*python3)/\/bin\/perl -w/;
  # sort the key of res, as it is index of lines
  my @l_no = sort{$a<=>$b} keys %res;
  foreach my $k (@l_no) {
    my @k_no = sort(keys %{$res{$k}});
    # set indetation space for the individual line in perl
    my $inde = "";
    $inde = $inde.$IND for (1..$layer);
    foreach my $kv (@k_no) {
      if ($kv =~ m/^(\s*)?print$/) { # print function
        $out_lines[$k]="print $res{$k}{print};\n";
      } elsif ($kv =~ m/^push$/) {   # push function
        $out_lines[$k]="$res{$k}{push};\n";
      } elsif ($kv =~ m/^pop$/) {    # pop function
        $out_lines[$k]="$res{$k}{pop};\n";
      } elsif ($kv =~ m/^len$/) {    # len function
        $out_lines[$k]="$res{$k}{len};\n";
      } elsif ($kv =~ m/^break$/) {  # break keyword translate to last
        $out_lines[$k]="last;\n";
      } elsif ($kv =~ m/^continue$/) { # continue keyword
        $out_lines[$k]="$kv;\n";
      } elsif ($kv =~ m/^import$/) {  # skip the import statement
        $out_lines[$k]="\n";
      } elsif ($kv =~ m/^(\s*)?(while|if|elif|else|foreach)$/) {
        # translate the while, if, elif, else, for loop
        if( $res{$k}{$kv} ne "}") {
          # differentiate the open and close curly bracket
          if ($kv eq "elif") {
            # translate elif to elsif to simplify the lexe_analysis
            $out_lines[$k]="elsif $res{$k}{$kv}\n";
          } else {
            # compose keyword with condition expression
            $out_lines[$k]="$kv $res{$k}{$kv}\n";
          }
          # increment indentation level
          $layer++;
        } else {
          # decrement indentation level
          $layer--;
          # reset indentation spave for the individual line in perl
          $inde = "";
          $inde = $inde.$IND for (1..$layer);
          # put the closing curly bracket
          $out_lines[$k]="$res{$k}{$kv}\n";
        }
      } elsif ($kv =~ m/^(\s.*)?(\$|%|@)\w.*$/) { # variable assignment
        $out_lines[$k]="$kv$res{$k}{$kv};\n";
      }
    }
    # prepend the indentation space for the individual line
    $out_lines[$k] = $inde.$out_lines[$k];
  }
}

my %re = lexe_analysis(@lines);

parser(%re);
# print out the translate codes
print "$_" for (@out_lines);
#my $i = 0;
#for (@out_lines) {
#  print "$i $_";
#  $i++;
#}

