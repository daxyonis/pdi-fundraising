UPDATE pdicampaign SET blocked=1, blocked_date=DATE("2019-06-15"), closed=1, closed_date=DATE("2019-06-15"), export_date=DATE("2019-06-15");
UPDATE pdicampaign SET blocked=0, blocked_date=null, closed=0, closed_date=null, export_date=null;
SELECT * FROM pdi.pdicampaign;