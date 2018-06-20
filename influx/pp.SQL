InfluxMeasurement=DailyStat
clearTable=false

with 
tab1 as (SELECT t1.SOURCE, (case when ATTRIB_03 in ('Микро','Малые') or ATTRIB_03 IS NULL
       then 'ММБ'
       else 'КСБ+'
end) SEGMENT, count(1) STILL_NOT_CLOSED FROM 
SIEBEL.CX_LEAD t1
LEFT join siebel.S_ORG_EXT2_FNX t2 on t2.row_id=t1.ACCOUNT_ID
WHERE                                                                                           
STAGE IN (select VAL from siebel.s_lst_of_val where type='SBRF_PRESALE_STAGE' AND DESC_TEXT='Неконечная')
GROUP BY t1.SOURCE, (case when ATTRIB_03 in ('Микро','Малые') or ATTRIB_03 IS NULL
       then 'ММБ'
       else 'КСБ+'
end)),

tab2 as (
SELECT t1.SOURCE,(case when ATTRIB_03 in ('Микро','Малые') or ATTRIB_03 IS NULL
       then 'ММБ'
       else 'КСБ+'
end) SEGMENT, count(1)TODAY FROM 
SIEBEL.CX_LEAD t1
LEFT join siebel.S_ORG_EXT2_FNX t2 on t2.row_id=t1.ACCOUNT_ID
WHERE
t1.CREATED > to_char(sysdate)
GROUP BY t1.SOURCE, (case when ATTRIB_03 in ('Микро','Малые') or ATTRIB_03 IS NULL
       then 'ММБ'
       else 'КСБ+'
end)),

tab3 as (
SELECT t1.SOURCE, (case when ATTRIB_03 in ('Микро','Малые') or ATTRIB_03 IS NULL
       then 'ММБ'
       else 'КСБ+'
end) SEGMENT, count(1)NOT_IN_WORK FROM 
SIEBEL.CX_LEAD t1
LEFT join siebel.S_ORG_EXT2_FNX t2 on t2.row_id=t1.ACCOUNT_ID
WHERE
t1.CREATED < sysdate - interval '6' HOUR 
AND t1.STAGE in ('00 Инициация', 'Новая', 'Назначение КМ')
GROUP BY t1.SOURCE, (case when ATTRIB_03 in ('Микро','Малые') or ATTRIB_03 IS NULL
       then 'ММБ'
       else 'КСБ+'
end)),

tabSeg as(
select 'КСБ+' SEGMENT from dual
union
select 'ММБ' SEGMENT from dual
)

select t0.val SOURCE__tag, tabSeg.Segment as segment__tag,NVL(t1.STILL_NOT_CLOSED,'0') STILL_NOT_CLOSED__num, NVL(t2.TODAY,'0') CREATED_TODAY__num, NVL(t3.NOT_IN_WORK,'0') NOT_IN_WORK__num, 'ПП' as "TYPE__tag"
from 
siebel.s_lst_of_val t0
LEFT JOIN tabSeg on tabSeg.Segment is not null
LEFT JOIN tab1 t1 on t1.SOURCE=t0.VAL and t1.SEGMENT=tabSeg.SEGMENT
LEFT JOIN tab2 t2 on t2.SOURCE=t0.VAL and t2.SEGMENT=t1.SEGMENT
LEFT JOIN tab3 t3 on t3.SOURCE=t0.VAL and t3.SEGMENT=t1.SEGMENT
where type='SBRF_OPTY_SOURCE' AND LANG_ID='RUS' AND VAL IN ('Единое окно','Сайт Банка','Сбербанк АСТ', 'АС Клиентская аналитика', 'СББОЛ КБ', 'Кампания продаж', 'CRM Корпоративный')
order by t0.val,t1.SEGMENT
