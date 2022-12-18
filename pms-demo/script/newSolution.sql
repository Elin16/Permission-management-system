# 1.1
# query last n days healthLog

select count(*)
from studentBelonging as b, healthLog as h
where b.ID=h.studentID
and datediff(curdate(), reportDate) < 5
group by b.classID;

# 1.2
# query entry permission
select b.*, s.entryPerm
from studentBelonging as b, student as s
where b.ID=s.ID;

# 1.3
# query entry/leave application, can select base on progress
select b.*, a.*
from studentBelonging as b, entryApplication as a
where b.ID=a.studentID
and progress='submitted';


select b.*, a.*
from studentBelonging as b, leaveApplication as a
where b.ID=a.studentID
  and progress='submitted';

# 1.4
# query off-campus time in the last year
# not know how to do





# 2.1
# submitted entryApplication/leaveApplication in last n days
select b.*, a.*
from studentBelonging as b, entryApplication as a
where b.ID=a.studentID
  and progress='submitted'
  and datediff(curdate(), date(applyTime)) < 5;

select b.*, a.*
from studentBelonging as b, leaveApplication as a
where b.ID=a.studentID
  and progress='submitted'
  and datediff(curdate(), date(applyTime)) < 5;

# 2.2
# n student submit most entryApplication
SELECT b.*, count(a.ID) AS applicationAmount
FROM studentBelonging AS t, entryApplication AS a
WHERE t.ID=a.studentID
GROUP BY t.ID
ORDER BY applicationAmount DESC
LIMIT 0,2;

SELECT t.*, count(app.ID) AS applicationAmount
FROM studentBelonging AS t, entryApplication AS app
WHERE t.ID=app.studentID
GROUP BY t.ID
ORDER BY applicationAmount DESC
LIMIT 0,2;


# 2.3
# n student have longest average off-campus time
# not know how to do



# 2.4 show-oos
# show off-campus students , number, info , and leave time
select * from
(select s.*, b.dptID, max(IOTime)
from studentBelonging as b, student as s, IOLog as io
where b.ID=s.ID and s.ID=io.studentID and inschool=0
group by b.ID) as t where t.ID=t.ID;

select t.dptID, count(*)  from
    (select s.*, b.dptID, max(IOTime)
     from studentBelonging as b, student as s, IOLog as io
     where b.ID=s.ID and s.ID=io.studentID and inschool=0
     group by b.ID) as t where t.dptID=1;

# 2.5 show-stay-oos
# show off-campus more than 24 hours student who has no active leaveApplication,
SELECT *
FROM studentBelonging
WHERE ID IN(
    SELECT studentID
    FROM (
             SELECT studentID, max(IOTime) as lastOut
             FROM IOLog
             WHERE IOType='out'
             GROUP BY studentID
         ) AS outOfSchool
    WHERE timediff(current_timestamp, lastOut) > '24:00:00'
)
  AND ID IN (
    (SELECT ID
     FROM student
     WHERE entryPerm > 0 AND ID NOT IN(
         SELECT ID
         FROM leaveApplication
         WHERE progress='submitted' OR progress='approved' OR progress='success'
     )
    )
) AND studentBelonging.dptID=1;

# 2.6 show-leave-is
# on-campus student who has submitted leaveApplication, number and info
select *
from studentBelonging as t, student as s
where t.ID=s.ID
and t.ID in(
    (
        select studentID
        from leaveApplication
        where progress='submitted' or progress='approved' or progress='success'
    )
) and s.inschool=1;

# 2.7 always-is
# students in school for last n days
SELECT *
FROM studentBelonging AS t, student AS s
WHERE t.ID=s.ID
AND t.ID IN (
    SELECT studentID
    FROM (
             SELECT studentID, max(IOTime) AS lastIn
             FROM IOLog
             WHERE IOType='in'
             GROUP BY studentID
         ) AS lastInRecord
    WHERE datediff(current_date, DATE(lastIn) ) > 1
) AND s.inschool=1;

# 2.8 show-exact-report
# show student who continue submitting healthReport for n days
SELECT *
FROM studentBelonging AS t
WHERE t.ID IN
      (SELECT c.studentID
          FROM  (SELECT h.studentID, max(reportTime) as maxT, min(reportTime) as minT
                 from healthLog as h
                 where datediff(current_date, h.reportDate) < 10
                 group by h.studentID
                ) as c
                where timediff(c.maxT, c.minT) < '00:01:00');


# 2.9
# campus having most IOLogs for every department
SELECT ranked.dptID, ranked.campusName
FROM(
     SELECT counted.dptID, counted.campusName,
            rank()OVER(PARTITION BY counted.dptID ORDER BY (number) DESC) AS io_rank
     FROM (
          SELECT t.dptID, io.campusName, count(*) AS number
          FROM studentBelonging AS t, IOLog AS io
          WHERE t.ID=io.studentID
          group BY t.dptID, io.campusName
         ) AS counted
     ) AS ranked
WHERE ranked.io_rank=1;

















