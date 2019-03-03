<?php
// COMP3311 18s1 Assignment 2
// Functions for assignment Tasks A-E
// Written by <<YOUR NAME>> (<<YOUR ID>>), May 2018

// assumes that defs.php has already been included

// Task A: get members of an academic object group

// E.g. list($type,$codes) = membersOf($db, 111899)
// Inputs:
//  $db = open database handle
//  $groupID = acad_object_group.id value
// Outputs:
//  array(GroupType,array(Codes...))
//  GroupType = "subject"|"stream"|"program"
//  Codes = acad object codes in alphabetical order
//  e.g. array("subject",array("COMP2041","COMP2911"))

function membersOf($db, $groupID)
{
    $flag = false;
    if (is_array($groupID)) {
        list($groupID, $flag) = $groupID;
    }
    $q = "select * from acad_object_groups where id = %d";
    $grp = dbOneTuple($db, mkSQL($q, $groupID));
    $q = "";
    $res = array();
    $inrel = array();
    if ($grp["gdefby"] == "pattern") {
        $grp["definition"] = preg_replace("/[{}]/", '', $grp["definition"]);
        $asres = preg_split("/[,;]/", $grp["definition"]);
        foreach ($asres as $i) {
            if ((preg_match('/^(GENG|GEN#|FREE|####|all|ALL).*\/F=/', $i)
                || preg_match('/F=.*/', $i)) && $flag) {
                list($pat, $fac) = preg_split("/\/F=|\//", $i);
                if (preg_match('/^F=/', $pat)) {
                    $tem = $fac;
                    $fac = $pat;
                    $pat = $tem;
                }
                $fac = preg_replace('/(\/)?F=/', '', $fac);
                $pat = preg_replace('/FREE/', '[^GEN.]', $pat);
                $pat = preg_replace('/GENG/', '^GEN.', $pat);
                $pat = preg_replace('/all|ALL/', '.{4}', $pat);
                $pat = preg_replace('/#|x/', '.', $pat);
                $fac = preg_replace('/!(.*)/', '[^$1]', $fac);
                if ($pat === '') {
                    $pat = '.';
                }
                $q = get_pattern_F($db, $groupID, $grp["gtype"], $fac, $pat);
                $r = dbQuery($db, $q);
                while ($t = dbNext($r)) {
                    $res[] = $t[0];
                }

            } else if (preg_match('/^(GENG|GEN#|FREE|####|all|ALL)/', $i)) {
                $inrel[] = $i;
            } else if (!preg_match('/^!/', $i)) {
                $i = preg_replace('/#|x/', '.', $i);
                $append = array();
                $q = get_pattern($db, $groupID, $grp["gtype"], $i);
                $r = dbQuery($db, $q);
                while ($t = dbNext($r)) {
                    $append[] = $t[0];
                }

                $res = array_merge($res, $append);
            }
        }
        foreach ($asres as $i) {
            if (preg_match('/^!/', $i)) {
                $i = preg_replace("/!/", '', $i);
                $i = str_replace("#", ".", $i);
                foreach ($res as $e) {
                    if (preg_match("/$i/", $e)) {
                        unset($res[array_search($e, $res)]);
                    }
                }
            }
        }
    } else {
        if ($grp["gdefby"] == "enumerated") {
            $q = get_enum($db, $groupID, $grp["gtype"], $res);
            $r = dbQuery($db, $q);
            while ($t = dbNext($r)) {
                $res[] = $t[0];
            }

        }
        if ($grp["gdefby"] == "query") {
            $q = dbQuery($db, $grp["definition"]);
            while ($t = dbNext($r)) {
                $res[] = $t[1];
            }

        }
    }
    sort($res);
    if (sizeof($inrel) != 0) {
        $res = array_merge($res, $inrel);
    }
    $res = array_unique($res);
    return array($grp["gtype"], $res); // stub
}

function get_enum($db, $gid, $type)
{
    $q = "select code from $type";
    $q .= "s where id in (select ";
    $q .= "$type from $type";
    $q .= "_group_members where ao_group in (select id from acad_object_groups where id = %d or parent = %d))";
    return mkSQL($q, $gid, $gid);
}

function get_pattern_F($db, $gid, $type, $fac, $pat)
{
    $q = "select code from $type";
    $q .= "s where facultyof(offeredby) in (select id from orgunits where unswid ~* %s) and code ~ %s";
    return mkSQL($q, $fac, $pat);
}

function get_pattern($db, $gid, $type, $pat)
{
    $q = "select code from $type";
    $q .= "s where code ~ %s";
    return mkSQL($q, $pat);
}

// Task B: check if given object is in a group

// E.g. if (inGroup($db, "COMP3311", 111938)) ...
// Inputs:
//  $db = open database handle
//  $code = code for acad object (program,stream,subject)
//  $groupID = acad_object_group.id value
// Outputs:
//  true/false

function inGroup($db, $code, $groupID)
{
    $q = "select * from acad_object_groups where id = %d";
    $grp = dbOneTuple($db, mkSQL($q, $groupID));
    list($type, $members) = membersOf($db, array($groupID, true));
    if ($grp["gdefby"] != "pattern") {
        if (in_array($code, $members, true)) {
            return true; // stub
        } else {
            return false;
        }
    } else {
        if (in_array($code, $members, true)) {
            return true; // stub
        } else {
            if (preg_match('/^(GENG|GEN#)/', $members[0])) {
                return (preg_match('/^GEN/', $code));
            } else if (preg_match('/^(FREE|all|ALL)/', $members[0])) {
                return (!preg_match('/^GEN/', $code));
            } else if (preg_match('/#/', $members[0])) {
                $pat = preg_replace('/#|x/', ".", $members[0]);
                return (preg_match("/^$pat/", $code));
            }
            return false;
        }
    }
}

// Task C: can a subject be used to satisfy a rule

// E.g. if (canSatisfy($db, "COMP3311", 2449, $enr)) ...
// Inputs:
//  $db = open database handle
//  $code = code for acad object (program,stream,subject)
//  $ruleID = rules.id value
//  $enr = array(ProgramID,array(StreamIDs...))
// Outputs:

function canSatisfy($db, $code, $ruleID, $enrolment)
{
    $flag = true;
    if (is_array($ruleID)) {
        list($ruleID, $flag) = $ruleID;
    }

    $rel_fac = fetch_fac($db, $enrolment);

    $q = "select facultyof(offeredby) from subjects where code = %s";
    $fac = dbOneValue($db, mkSQL($q, $code));

    $q = "select * from rules r where r.id = %d";
    $rule = dbOneTuple($db, mkSQL($q, $ruleID));

    if (preg_match('/^(LR|MR|WM|IR)/', $rule['type'])) {
        return false;
    } else if (preg_match('/^DS/', $rule['type'])) { //stream
        return inGroup($db, $code, $rule["ao_group"]);
    } else if (preg_match('/^(CC|PE|FE|GE)/', $rule['type'])) {
        if ($rule["ao_group"] === null) {
            return false;
        }
        if (preg_match('/^[A-Z]{4}[0-9]{4}$/', $code)) { // subject
            list($type, $members) = membersOf($db, $rule["ao_group"]);
            if (preg_match('/^(GENG|GEN#)/', $members[0]) && inGroup($db, $code, $rule["ao_group"])) {
                return (!in_array($fac, $rel_fac, true));
            } else if (inGroup($db, $code, $rule["ao_group"])) {
                return true;
            }
        }
    } else if (preg_match('/^RQ/', $rule['type']) && $flag) {
        $q = "select * from acad_object_groups a, rules r where a.id = r.ao_group and r.id = %d";
        $rule = dbOneTuple($db, mkSQL($q, $ruleID));
        if ($rule["ao_group"] === null) {
            return false;
        }
        if (preg_match('/^[0-9]{4}$/', $code) &&
            $rule["gtype"] == "program") {
            return inGroup($db, $code, $rule["ao_group"]);
        } else if (preg_match('/^[A-Z]{5}[A-Z0-9]$/', $code) && $rule["gtype"] == "stream") {
            return inGroup($db, $code, $rule["ao_group"]);
        } else if (preg_match('/^[A-Z]{4}[0-9]{4}$/', $code) && $rule["gtype"] == "subject") {
            return inGroup($db, $code, $rule["ao_group"]);
        }
    }
    return false; // stub
}

function fetch_fac($db, $enrolment)
{
    $facs = array();
    list($prog, $streams) = $enrolment;
    $q = "select facultyof(offeredby) from programs where id = %d";
    $r = dbOneValue($db, mkSQL($q, $prog));
    $facs[] = $r;
    foreach ($streams as $stream) {
        $q = "select facultyof(offeredby) from streams where id = %d";
        $r = dbOneValue($db, mkSQL($q, $stream));
        $facs[] = $r;
    }
    $facs = array_unique($facs);
    return $facs;
}
// Task D: determine student progress through a degree

// E.g. $vtrans = progress($db, 3012345, "05s1");
// Inputs:
//  $db = open database handle
//  $stuID = People.unswid value (i.e. unsw student id)
//  $semester = code for semester (e.g. "09s2")
// Outputs:
//  Virtual transcript array (see spec for details)

function progress($db, $stuID, $term)
{
    $oldTerm = $term;
    $flag = true;
    if (is_array($stuID)) {
        list($stuID, $flag) = $stuID;
    }

    $q = "select id, (select code from programs where id = p.program) as code, program from Program_enrolments p where student=%d and semester <= %d";
    $pe = dbOneTuple($db, mkSQL($q, $stuID, $term));

    if (empty($pe)) {
        $q = "select id, (select code from programs where id = p.program) as code, program, semester from Program_enrolments p where student=%d order by semester limit 1";
        $pe = dbOneTuple($db, mkSQL($q, $stuID, $term));
        $term = $pe["semester"];
    }

    $q = "select stream from Stream_enrolments where partof=%d";
    $r = dbQuery($db, mkSQL($q, $pe["id"]));
    $streams = array();
    while ($t = dbNext($r)) {$streams[] = $t[0];}
    $enrolment = array($pe["program"], $streams); // ProgID,StreamIDs

    //2. Get rules
    // partof in stream_enrolment referencing program enrolments
    // therefore (select stream from Stream_enrolments where partof= %d) as subquery to get stream rules
    $q = "(select * from rules_for_prog r where code = %s) union (select s.stream::text as code, r.id,  r.type, r.min, r.max, r.ao_group from stream_rules s, rules r where s.rule = r.id and stream in (select stream from Stream_enrolments where partof= %d)) order by type, id;";
    $r = dbQuery($db, mkSQL($q, $pe['code'], $pe['id']));
    list($stats, $rules) = get_rules($db, $r);

    //3. Get the transcript for specific semester and term
    if ($flag) {
        $q = "select * from transcript(%d, %d)";
    } else {
        $q = "select code,(select id from semesters where termname(id) = f.term )as term, name, mark, grade, uoc from transcript(%d, %d) as f where code is not null";
    }

    $r = dbQuery($db, mkSQL($q, $stuID, $term)); // stub
    $accepted_grades = array('PT', 'PC', 'PS', 'CR', 'DN', 'HD', 'A', 'B', 'SY', 'C');

    while ($t = dbNext($r)) {
        list($code, $term, $title, $mark, $grade, $uoc) = $t;
        if (preg_match('/.*WAM.*/', $title)) {
            $res[] = array("Overall WAM", $mark, $uoc);
        } else if ($grade === null && $uoc <= 6 && $flag) {
            $res[] = array($code, $term, $title, $mark, $grade, null, 'Incomplete. Does not yet count');
        } else {
            $stop = true;
            foreach ($stats as $rulename => $rule) {
                $rule = $rule["rule"];
                if ($stop) {
                    if (preg_match('/^GE/', $rule["type"]) && !preg_match('/^GEN/', $code)) {
                        continue;
                    }

                    if ($stats[$rule["name"]]["todo"] && canSatisfy($db, $code, array($rule["id"], false), $enrolment)) {
                        if ((!($flag && $grade != null)) || in_array($grade, $accepted_grades, true)) {
                            if ($oldTerm < $term && !$flag) {
                                continue;
                            }

                            if ($stats[$rule["name"]]["todo"]) {
                                $res[] = ($flag) ? array($code, $term, $title, $mark, $grade, $uoc, $rule["name"]) : array($code, $term, $title, $mark, $grade, $uoc, $rule['id'], $rule['ao_group']);
                                $stats[$rule["name"]]["total"] += $uoc;
                                $stats[$rule["name"]]["todo"] -= $uoc;
                                $stop = false;
                            }
                        } else {
                            $res[] = array($code, $term, $title, $mark, $grade, $uoc, "Failed. Does not count");
                            $stop = false;
                        }
                    }
                }
            }
            if ($stop) {
                $res[] = ($flag) ? array($code, $term, $title, $mark, $grade, $uoc, 'Fits no requirement. Does not count') : array($code, $term, $title, $mark, $grade, $uoc, null, null);
            }

        }
    }
    if (!$flag) {
        $stats = array_merge($stats, $rules);
        return array($stats, $res);
    }
    //4. TO-DO
    foreach ($stats as $k => $v) {
        if ($v["todo"] != 0) {
            $v = $v["total"] . " UOC so far; need " . $v["todo"] . " UOC more";
            $res[] = array($v, $k);
        }
    }
    return $res;
}

function get_rules($db, $r)
{
    $stats = array();
    $rules = array();
    $fg = array();
    $cp = array();
    while ($t = dbNext($r)) {
        $t["name"] = ruleName($db, $t["id"]);

        list($code, $id, $type, $min, $max, $ao_group) = $t;
        if (preg_match('/^CC|PE/', $t["type"])) {
            if (!array_key_exists($t["id"], $stats)) {
                $cp[$t["name"]] =
                array("todo" => $t["min"], "total" => 0, "rule" => $t);
            }
        } else if (preg_match('/^FE|GE|LR/', $t["type"])) {
            if (!array_key_exists($t["id"], $stats)) {
                $fg[$t["name"]] =
                array("todo" => $t["min"], "total" => 0, "rule" => $t);
            }
        } else if (preg_match('/^MR|RC|RQ/', $t["type"])) {
            $rules[$t["name"]] =
            array("todo" => $t["min"], "total" => 0, "rule" => $t);
        }
    }
    $res = array(array_merge($cp, $fg), $rules);
    return $res;
}

// Task E:

// E.g. $advice = advice($db, 3012345, 162, 164)
// Inputs:
//  $db = open database handle
//  $studentID = People.unswid value (i.e. unsw student id)
//  $currTermID = code for current semester (e.g. "09s2")
//  $nextTermID = code for next semester (e.g. "10s1")
// Outputs:
//  Advice array (see spec for details)

function advice($db, $studentID, $currTermID, $nextTermID)
{
    // get the course codes are equivalent to those the student has taken
    $except = get_except_course($db, $studentID, $currTermID);

    // get transcript take incompleted courses into progress, and get the rules in the stats to select course
    list($stats, $trans) = progress($db, array($studentID, false), $currTermID);

    // calculte current UOC
    $currentUOC = 0;
    foreach ($trans as $e) {
        list($code, $term, $title, $mark, $grade, $uoc, $r_id, $r_ao_group) = $e;
        $currentUOC += $uoc;
    }

    // make the base sql to select next semester courses offering
    $base = "with tran_codes as (select code from transcript(%d, %d) where grade is null or grade ~'PT|PC|PS|CR|DN|HD|A|B|C')
    (select s.code from subjects s, courses c where s.id = c.subject and c.semester =%d  except (select code from tran_codes))";
    $base = mkSQL($base, $studentID, $currTermID, $nextTermID);
    list($passed, $sub_p) = get_passed_subjects($db, $studentID, $currTermID);
    $q = "select career from programs p, program_enrolments e where p.id=e.program and student = %d and semester <= %d";
    $career = dbOneValue($db, mkSQL($q, $studentID, $currTermID));
    if (!$career) {
        $q = "select career from programs p, program_enrolments e where p.id=e.program and student = %d order by semester limit 1";
        $career = dbOneValue($db, mkSQL($q, $studentID, $currTermID));
    }
    $del = array();
    $res = array();
    foreach ($stats as $rulename => $stat) {
        $rule = $stat["rule"];

        switch ($rule["type"]) {
            case 'GE':
                $res[] = array("GenEd...", "General Education (many choices)", $stat["todo"], $rulename);
                break;
            case 'FE':
                $res[] = array("Free....", "Free Electives (many choices)", $stat["todo"], $rulename);
                break;
            case 'LR':
                $res[] = array("Limit...", $rulename . " (many choices)", $stat["todo"], $rulename);
                break;
            case 'CC':
                $res = merge_course($db, $base, $rule, $except, $passed, $sub_p, $rulename, $career, $res);
                break;
            case 'PE':
                $res = merge_course($db, $base, $rule, $except, $passed, $sub_p, $rulename, $career, $res);
                break;
            case 'MR':
                if ($currentUOC < $rule["min"]) {
                    foreach ($res as $r) {
                        $code = strtoupper($r[0]);
                        if (inGroup($db, $code, $rule["ao_group"])) {
                            $del[] = array_search($r, $res);
                        }
                    }
                }
                break;
            case 'RQ':
                if ($currentUOC < $rule["min"]) {
                    foreach ($res as $r) {
                        $code = strtoupper($r[0]);
                        if (inGroup($db, $code, $rule["ao_group"])) {
                            $del[] = array_search($r, $res);
                        }
                    }
                }
                break;
            case 'RC':
                if ($currentUOC < $rule["min"]) {
                    foreach ($res as $r) {
                        $code = strtoupper($r[0]);
                        if (inGroup($db, $code, $rule["ao_group"])) {
                            $del[] = array_search($r, $res);
                        }
                    }
                }
        }
    }
    $del = array_unique($del);
    foreach ($del as $i) {
        unset($res[$i]);
    }
    return $res; // stub
}
function merge_course($db, $base, $rule, $except, $passed, $sub_p, $rulename, $career, $res)
{
    $codes = course_code($db, $base, $rule, $except);
    foreach ($codes as $code) {
        $s = get_UOC_name($db, $code);
        list($pre, $careers) = get_prereqs($db, $code);
        if ($code == "COMP4910") {
            continue;
        }

        if (empty($pre) && $career == $s["career"]) {
            $exist = false;
            foreach ($res as $r) {
                if ($code == $r[0]) {
                    $exist = true;
                }

            }
            if (!$exist) {
                $res[] = array($code, $s["name"], $s["uoc"], $rulename);
            }

        } else if (!empty($passed) && count(array_intersect($pre, $passed))) {
            if ($career == $s["career"] || in_array($career, $careers, true)) {

                $exist = false;
                foreach ($res as $r) {
                    if ($code == $r[0]) {$exist = true;}
                }
                if (!$exist) {
                    $res[] = array($code, $s["name"], $s["uoc"], $rulename);
                }

            }
        } else {
            if ($career == $s["career"] || in_array($career, $careers, true)) {
                if (count(array_intersect($pre, $passed))) {
                    // echo "$code, $pre\n";
                    // var_dump(array_intersect($pre, $sub_p));
                    $exist = false;
                    foreach ($res as $r) {
                        if ($code == $r[0]) {
                            $exist = true;
                        }
                    }
                    if (!$exist) {
                        $res[] = array($code, $s["name"], $s["uoc"], $rulename);
                    }

                }
            }
        }
    }
    return $res;
}
function course_code($db, $base, $rule, $diff)
{
    $codes = array();
    $q = "select * from acad_object_groups where id = %d";
    $grp = dbOneTuple($db, mkSQL($q, $rule["ao_group"]));
    if ($grp["gdefby"] == "enumerated") {
        $q = get_enum($db, $rule["ao_group"], $grp["gtype"]);
        $q = $base . " intersect (" . $q . ")";
        $codes = get_codes($db, $q);
    } else if ($grp["gdefby"] == "query") {
        $q = preg_replace('/id,/', '', $grp["definition"]);
        $q = $base . " intersect (" . $q . ")";
        $codes = get_codes($db, $q);
    } else {

        $grp["definition"] = preg_replace("/[{}]/", '', $grp["definition"]);
        $p = "";
        $asres = preg_split("/[,;]/", $grp["definition"]);
        foreach ($asres as $i) {
            if ((preg_match('/^(GENG|GEN#|FREE|####|all|ALL).*/', $i)
                || preg_match('/F=.*/', $i))) {
                list($pat, $fac) = preg_split("/\/F=|\//", $i);
                if (preg_match('/^F=/', $pat)) {
                    $tem = $fac;
                    $fac = $pat;
                    $pat = $tem;
                }
                $fac = preg_replace('/(\/)?F=/', '', $fac);
                $pat = preg_replace('/FREE/', '[^GEN.]', $pat);
                $pat = preg_replace('/GENG/', '^GEN.', $pat);
                $pat = preg_replace('/all|ALL/', '.{4}', $pat);
                $pat = preg_replace('/#/', '.', $pat);
                $fac = preg_replace('/!(.*)/', '[^$1]', $fac);

                if ($pat === '') {
                    $pat = '.';
                }
                $q = get_pattern_F($db, $rule["ao_group"], $grp["gtype"], $fac, $pat);

                $p .= " union ( $q )";
            } else if (!preg_match('/^!/', $i)) {
                $i = str_replace("#", ".", $i);
                $append = array();
                $q = get_pattern($db, $rule["ao_group"], $grp["gtype"], $i);
                $p .= " union ( $q )";
            }
        }
        $p = substr($p, 6);
        $q = $base . " intersect (" . $p . ")";
        $codes = get_codes($db, $q);
    }
    $codes = array_diff($codes, $diff);
    return $codes;
}
function get_codes($db, $q)
{
    $codes = array();
    $q .= ' order by code';
    $r = dbQuery($db, $q);
    while ($t = dbNext($r)) {
        $codes[] = $t[0];
    }

    return $codes;
}
function get_UOC_name($db, $code)
{
    $q = "select uoc, name ,career from subjects where code = %s";
    $s = dbOneTuple($db, mkSQL($q, $code));
    return $s;
}
function get_except_course($db, $stuID, $sem)
{
    $q = "with subject_code as (select code from transcript(%d, %d) where grade is null or grade ~'PT|PC|PS|CR|DN|HD|A|B|C'), ";
    $q .= "ao as ((select distinct(excluded) as group from subjects where code in (select code from subject_code) and excluded is not null) union (select distinct(equivalent) as group from subjects where equivalent is not null and code in (select code from subject_code))) select id from acad_object_groups where id in (select * from ao)";
    $p = mkSQL($q, $stuID, $sem);
    $codes = array();
    $r = dbQuery($db, mkSQL($q, $stuID, $sem));
    while ($t = dbNext($r)) {
        list($type, $tem) = membersOf($db, array($t[0], true));
        $codes = array_merge($codes, $tem);
    }
    array_unique($codes);
    return $codes;
}

function get_passed_subjects($db, $stuID, $term)
{
    $res = array();
    $equ = array();
    $q = "select code, excluded, equivalent from subjects where code in (select code from (select code from transcript(%d, %d) where grade is null or grade ~  'PT|PC|PS|CR|DN|HD|A|B|C') as foo where code is not null)";
    $r = dbQuery($db, mkSQL($q, $stuID, $term));
    while ($t = dbNext($r)) {
        $res[] = $t[0];
        if ($t[1]) {
            list($type, $ms) = membersOf($db, $t[1]);
            $equ = array_merge($equ, $ms);
        }
        if ($t[2]) {
            list($type, $ms) = membersOf($db, $t[2]);
            $equ = array_merge($equ, $ms);
        }
    }
    sort($equ);
    array_unique($equ);
    return array($res, $equ);
}

function get_prereqs($db, $code)
{
    $q = "select a.id, s.career from acad_object_groups a, subject_prereqs s, rules r where r.id = s.rule and  s.subject = (select id from subjects where code = %s) and a.id = r.ao_group";
    $r = dbQuery($db, mkSQL($q, $code));
    $careers = array();
    $members = array();
    while ($t = dbNext($r)) {
        list($grp, $career) = $t;
        list($type, $mem) = membersOf($db, $grp);
        $members = array_merge($members, $mem);
        $careers[] = $career;
    }
    $careers = array_unique($careers);
    $members = array_unique($members);

    return array($members, $careers);
}
