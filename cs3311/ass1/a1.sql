-- COMP3311 18s1 Assignment 1
-- Written by YOUR_NAME (YOUR_STUDENT_ID), April 2018

-- Q1: ...

create or replace view Q1(unswid, name)
  as
  SELECT people.unswid,
  people.name
  FROM people
  WHERE (people.id IN ( SELECT course_enrolments.student
        FROM course_enrolments
        GROUP BY course_enrolments.student
        HAVING count(*) > 65))

  ;

  -- Q2: ...

create or replace view Q2(nstudents, nstaff, nboth)
  as
  SELECT ( SELECT count(x.id) AS count
      FROM ( SELECT students.id
        FROM students
        EXCEPT
        SELECT staff.id
        FROM staff) x) AS nstudents,
  ( SELECT count(x.id) AS count
    FROM ( SELECT staff.id
      FROM staff
      EXCEPT
      SELECT students.id
      FROM students) x) AS nstaff,
  ( SELECT count(b.id) AS count
    FROM ( SELECT staff.id
      FROM staff
      INTERSECT
      SELECT students.id
      FROM students) b) AS nboth;


  -- Q3: ...

create or replace view Q3(name, ncourses)
  as
  with staff_count as (
      select staff, count(*) from course_staff
      where role in (
        select id from staff_roles where name ='Course Convenor'
        ) group by staff

      ) select p.name, c.count as ncourses from staff_count c, people p
  where p.id = c.staff and c.count = (select max(count) from
      staff_count);

  -- Q4: ...

create or replace view Q4a(id)
  as
  select p.unswid as id from program_enrolments pe, people p
  where pe.student = p.id
  and semester = (
      select id from semesters where year = 2005 and term='S2'
      )
  and program in (
      select id from programs where name = 'Computer Science' and code='3978'
      );


create or replace view Q4b(id)
  as
  select p.unswid from people p, program_enrolments pe, stream_enrolments se
  where p.id = pe.student and pe.id = se.partof
  and pe.semester = (
      select id from semesters where year = 2005 and term='S2'
      ) and se.stream in (
        select id from streams where name ~ 'Software Engineering' and code = 'SENGA1'
        );


create or replace view Q4c(id)
  as
  select p.unswid as id
  from program_enrolments pe,
  people p
  where pe.student = p.id and
  semester = (select id from semesters where year = 2005 and term='S2') and
  program in (
      select distinct(program) from program_group_members
      where ao_group in (
        select id from acad_object_groups
        where name ~ 'CSE|Computer Science and Eng'
        and gtype = 'program'
        )
      );

  -- Q5: ...
  CREATE OR REPLACE VIEW public.q5 AS
  WITH ccount AS (
      SELECT count(orgunits.id) AS count,
      facultyof(orgunits.id) AS facultyof
      FROM orgunits
      WHERE orgunits.utype = (( SELECT orgunit_types.id
          FROM orgunit_types
          WHERE orgunit_types.name::text = 'Committee'::text))
      AND facultyof(orgunits.id) IS NOT NULL
      GROUP BY (facultyof(orgunits.id))
      )
  SELECT o.name
  FROM ccount c,
  orgunits o
  WHERE o.id = c.facultyof AND c.count = (( SELECT max(ccount.count) AS
        max
        FROM ccount));

  -- Q6: ...

  create or replace function Q6(integer) returns text
  as $$
  select name from people where id=$1 or unswid =$1;

  $$ language sql
  ;

  -- Q7: ...
  create or replace function Q7(text)
RETURNS TABLE(course text, year integer, term text, convenor text)
  LANGUAGE sql
  AS $function$
  with course_sub as (
      select c.id, semester from courses c, subjects s where c.subject =
      s.id and code = $1),
  sub_sem as (
      select id, year, term from semesters where id in (select semester from
        course_sub) )
  select $1 as course, year, term::text , p.name as convenor from people
  p, course_staff s, course_sub c, sub_sem ss where p.id = staff and
  s.course = c.id and  role in ( select id from staff_roles where name
      ='Course Convenor') and c.semester = ss.id and  course in ( select id
        from course_sub);
      $function$
      ;

      -- Q8: ...

create or replace function Q8(integer)
  RETURNS SETOF newtranscriptrecord
  LANGUAGE plpgsql
  AS $function$
  DECLARE
  rec newTranscriptRecord;
  UOCtotal integer := 0;
  UOCpASsed integer := 0;
  wsum integer := 0;
  wam integer := 0;
  x integer;
  BEGIN
  SELECT s.id into x
FROM  Students s JOIN People p ON (s.id = p.id)
  WHERE  p.unswid = $1;
  if (not found) then
  raise EXCEPTION 'Invalid student %',$1;
  end if;
  for rec in
  WITH pro_sem AS (
      SELECT pro.code, pe.semester
      FROM program_enrolments pe JOIN programs pro ON 	(pro.id = pe.program)
      WHERE pe.student = (select id FROM people WHERE unswid = $1)
      )
  SELECT su.code,
  substr(t.year::text,3,2)||lower(t.term),
  ps.code,
  substr(su.name,1,20),
  e.mark, e.grade, su.uoc
  FROM   People p
  JOIN Students s ON (p.id = s.id)
  JOIN Course_enrolments e ON (e.student = s.id)
  JOIN Courses c ON (c.id = e.course)
  JOIN Subjects su ON (c.subject = su.id)
  JOIN Semesters t ON (c.semester = t.id)
JOIN pro_sem ps ON (ps.semester = t.id)
  WHERE  p.unswid = $1
  order  by t.starting, su.code
  loop
  if (rec.grade = 'SY') then
  UOCpASsed := UOCpASsed + rec.uoc;
  elsif (rec.mark is not null) then
  if (rec.grade in ('PT','PC','PS','CR','DN','HD','A','B','C')) then
  -- only counts towards creditted UOC
  -- if they pASsed the course
  UOCpASsed := UOCpASsed + rec.uoc;
  end if;
  -- we count fails towards the WAM calculation
  UOCtotal := UOCtotal + rec.uoc;
  -- weighted sum bASed ON mark and uoc for course
  wsum := wsum + (rec.mark * rec.uoc);
  -- don't give UOC if they failed
  if (rec.grade not in ('PT','PC','PS','CR','DN','HD','A','B','C')) then
  rec.uoc := 0;
  end if;

  end if;

  return next rec;
  end loop;
  if (UOCtotal = 0) then
  rec := (null,null,null,'No WAM available',null,null,null);
  else
  wam := wsum / UOCtotal;
rec := (null,null,null,'Overall WAM',wam,null,UOCpASsed);
end if;
-- append the lASt record containing the WAM
return next rec;
end;
$function$;



-- Q9: ...

create or replace function Q9(integer)
  returns setof AcObjRecord
LANGUAGE plpgsql
AS $function$
DECLARE
        rec AcObjRecord;
  kind record;
  temp record;
  c text;
BEGIN
       SELECT gtype, gdefby, definition into kind
        FROM   acad_object_groups WHERE  id = $1;
  if kind.gdefby != 'pattern' then
    return;
  else
    if kind.definition ~ '\{*\}' or kind.definition ~ '.*/F=.*' then
      return;
    else
                        if kind.gtype = 'subject' then
      for temp in (select * from  regexp_split_to_table(kind.definition,
E'\,') as pattern)
                        Loop
        for c in (select code from subjects where code ~(select
replace(temp::text,'#','.')))
        LOOP
          rec.objtype := kind.gtype;
          rec.object := c;
          return next rec;
        end loop;
      end loop;
                        elsif kind.gtype = 'program' then
                        for temp in (select * from
regexp_split_to_table(kind.definition, E'\,') as pattern)
                        Loop
        for c in (select code from programs where code ~(select
replace(temp::text,'#','.')))
        LOOP
          rec.objtype := kind.gtype;
          rec.object := c;
          return next rec;
        end loop;
      end loop;
                        else
                        for temp in (select * from
regexp_split_to_table(kind.definition, E'\,') as pattern)
                        Loop
                                raise NOTICE 'stream object group %
%',$1, kind ;
        for c in (select code from streams where code ~(select
replace(temp::text,'#','.')))
        LOOP
          rec.objtype := kind.gtype;
          rec.object := c;
          return next rec;
        end loop;
      end loop;
                        end if;
    end if;
        end if;
end;
$function$;

