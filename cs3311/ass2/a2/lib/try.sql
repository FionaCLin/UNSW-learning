create or replace function get_course_offering
(term integer, groupId integer) returns setof text
LANGUAGE plpgsql
AS $function$
DECLARE
  kind record;
  temp record;
  c text;
BEGIN
  SELECT gtype, gdefby, definition
  into kind
  FROM acad_object_groups
  WHERE  id = $1;


  if kind.gdefby != 'pattern' then
  RAISE NOTICE 'hello, world!';
  end if;
  return;
end;
$function$;


RAISE NOTICE 'hello, world!';
