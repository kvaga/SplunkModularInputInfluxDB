InfluxMeasurement=LeadsBySource
clearTable=true

SELECT a.SOURCE as source__tag, a.val as count__num, to_char(a.hh,'YYYY-MM-DD HH24:MI:SS') time
FROM(
  select source, count(0) as val, trunc(created,'HH24') as hh 
  from SIEBEL.CX_LEAD
  WHERE created > sysdate-30   
  GROUP BY SOURCE, trunc(created,'HH24')) a