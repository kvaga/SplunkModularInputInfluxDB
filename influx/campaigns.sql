InfluxMeasurement=CampaignsStatus
clearTable=true

with 
tab1 as (
SELECT
DISTINCT (REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1)) Campaign_Code FROM SIEBEL.CX_LD_PROD_OFFR t1
WHERE REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1) is not null
),

tab2 as (
SELECT
REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1) Campaign_Code, COUNT(STATUS) PROD_OF_NOT_CLOSED, COUNT(DISTINCT ACCOUNT_ID) COUNT_OF_REL_CLIENTS  
FROM SIEBEL.CX_LD_PROD_OFFR t1
WHERE
STATUS IN (select VAL from siebel.s_lst_of_val where type='SBRF_PROD_OFFER_STATUS' AND DESC_TEXT='Неконечный')
GROUP BY REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1)
),

tab3 as (
SELECT
REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1) Campaign_Code,
COUNT(1) TODAY, COUNT(DISTINCT ACCOUNT_ID) REL_CLIENTS_TODAY
FROM SIEBEL.CX_LD_PROD_OFFR t1
WHERE
t1.CREATED > to_char(sysdate)
GROUP BY REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1)
),

tab4 as (
SELECT 
REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1) Campaign_Code, COUNT(STATUS) PROD_OF_CLOSED, COUNT(DISTINCT OPTY_ID) OPEN_OPPTY 
FROM SIEBEL.CX_LD_PROD_OFFR t1
WHERE 
STATUS IN (select VAL from siebel.s_lst_of_val where type='SBRF_PROD_OFFER_STATUS' AND DESC_TEXT='Конечный')
AND
CREATED > sysdate - interval '1' YEAR 
GROUP BY REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1)
),

tab5 as (
SELECT 
REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1) Campaign_Code, COUNT(1) NOT_IN_WORK 
FROM SIEBEL.CX_LD_PROD_OFFR t1
WHERE
STATUS = 'Новое предложение' 
AND
CREATED < sysdate - interval '6' HOUR 
GROUP BY REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1) 
),

tab6 as (
SELECT 
REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1) Campaign_Code, COUNT(DISTINCT ACCOUNT_ID) ORG_HAVE_CALLED 
FROM SIEBEL.CX_LD_PROD_OFFR t1
WHERE 
STATUS in ('Продукт не интересен', 'Создана сделка')
GROUP BY REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1)
),

tab7 as (
SELECT 
REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1) Campaign_Code, COUNT(DISTINCT ACCOUNT_ID) ORG_DEAL_CREATED 
FROM SIEBEL.CX_LD_PROD_OFFR t1
WHERE 
STATUS = 'Создана сделка' 
GROUP BY REGEXP_SUBSTR (t1.DESCRIPTION,'\((\w{2,3}-[A-ZА-Я0-9-]+-\d{4})\)',1,1,null,1)
)

select /*+PARALLEL(128)*/ 
tab1.Campaign_Code as CAMPAIGN_CODE__tag, NVL(tab2.PROD_OF_NOT_CLOSED,'0') PROD_OF_NOT_CLOSED__int, NVL(tab2.COUNT_OF_REL_CLIENTS,'0') COUNT_OF_REL_CLIENTS__int,
NVL(tab3.TODAY,'0') TODAY__int, NVL(tab3.REL_CLIENTS_TODAY,'0') REL_CLIENTS_TODAY__int, NVL(tab4.PROD_OF_CLOSED,'0') PROD_OF_CLOSED__int, 
NVL(tab4.OPEN_OPPTY,'0') OPEN_OPPTY__int, NVL(tab5.NOT_IN_WORK,'0') NOT_IN_WORK__int, NVL(tab6.ORG_HAVE_CALLED,'0') ORG_HAVE_CALLED__int, NVL(tab7.ORG_DEAL_CREATED,'0') ORG_DEAL_CREATED__int,
(case 
when tab6.ORG_HAVE_CALLED IS NULL OR tab7.ORG_DEAL_CREATED IS NULL then '-'
else to_char(ROUND(tab7.ORG_DEAL_CREATED/tab6.ORG_HAVE_CALLED,2),'FM90D90') 
end) as CONVERSION__float
from 
tab1
LEFT JOIN tab2 on tab2.Campaign_Code=tab1.Campaign_Code
LEFT JOIN tab3 on tab3.Campaign_Code=tab1.Campaign_Code
LEFT JOIN tab4 on tab4.Campaign_Code=tab1.Campaign_Code
LEFT JOIN tab5 on tab5.Campaign_Code=tab1.Campaign_Code
LEFT JOIN tab6 on tab6.Campaign_Code=tab1.Campaign_Code
LEFT JOIN tab7 on tab7.Campaign_Code=tab1.Campaign_Code