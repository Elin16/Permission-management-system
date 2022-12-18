# 1.1
# query last n days healthLog
select *
from studentBelonging as b, healthLog as h
where b.ID=h.studentID
and datediff(curdate(), reportDate) < 5;

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
SELECT b.*, count(a.ID) as applicationAmount
FROM studentBelonging as b, entryApplication as a
where b.ID=a.studentID
GROUP BY b.ID
order by applicationAmount desc
limit 0,2;


# 2.3
# n student have longest average off-campus time
# not know how to do



# 2.4
# show off-campus students , number, info , and leave time
select s.*, b.dptID, max(IOTime)
from studentBelonging as b, student as s, IOLog as io
where b.ID=s.ID and s.ID=io.studentID and inschool=0
group by b.ID;

# 2.5
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

# 2.6
# on-campus student who has submitted leaveApplication, number and info
select *
from studentBelonging as b, student as s
where b.ID=s.ID
and b.ID in(
    (
        select studentID
        from leaveApplication
        where progress='submitted' or progress='approved' or progress='success'
    )
) and s.inschool=1;

# 2.7
# students in school for last n days
select *
from studentBelonging as b, student as s
where b.ID=s.ID
and b.ID in (
    select studentID
    from (
             select studentID, max(IOTime) as lastIn
             from IOLog
             where IOType='in'
             group by studentID
         ) as c
    where datediff(current_date, DATE(lastIn) ) > 1
) and s.inschool=1

# 2.8
#

# 2.9
# campus having most IOLogs for every department
select



















