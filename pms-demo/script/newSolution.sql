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
    WHERE current_timestamp-'24:00:00' > lastOut
)
  AND ID IN (
    (SELECT ID
     FROM student
     WHERE entryPerm > 0 AND inschool=0 AND ID NOT IN(
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
                where c.maxT-'00:01:00' < c.minT );


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

# out of school total time
SELECT t.ID, dateDiff(date(ins), date(oos)) + dateDiff(firstIsIn*(curdate()-365), lastIsOut*curdate())
FROM (
    SELECT studentID, SUM(IOTime) as ins
    FROM IOLog
    WHERE datediff(curdate(),date(IOtime)) < 365 AND IOType='in'
    GROUP BY studentID
         ) as a,
     (
         SELECT studentID, SUM(IOTime) as oos
         FROM IOLog
         WHERE datediff(curdate(),date(IOtime)) < 365  AND IOType='out'
         GROUP BY studentID
     ) as b,
     studentBelonging as t,
     # c d 表只能确定至少有一条in和out的学生的记录
     # 只有一条in/out的学生怎么办呢
     # 只有in：in - year begin
     # 只有out：out - year end

     (

         # 考虑minIn不存在，即没有出校记录，这部分学生就不存在c1表里了
        SELECT c1.studentID, (TIMEDIFF(minInTime,minOutTime) < 0) as firstIsIn
        FROM (
                 SELECT studentID, MIN(IOTime) as minInTime
                 FROM IOLog
                 WHERE IOType='in'
                 GROUP BY studentID
        ) as c1,
             (
                 SELECT studentID, MIN(IOTime) as minOutTime
                 FROM IOLog
                 WHERE IOType='out'
                 GROUP BY studentID
             ) as c2
        WHERE c1.studentID=c2.studentID
    ) as c,
     (
         # 考虑maxOut不存在，即没有入校记录，这部分学生就不存在d2表里了
         SELECT d1.studentID, (TIMEDIFF(minInTime,minOutTime) < 0) as firstIsIn
         FROM (
                  SELECT studentID, MAX(IOTime) as maxInTime
                  FROM IOLog
                  WHERE IOType='in'
                  GROUP BY studentID
              ) as d1,
              (
                  SELECT studentID, MIN(IOTime) as maxOutTime
                  FROM IOLog
                  WHERE IOType='out'
                  GROUP BY studentID
              ) as d2
         WHERE d1.studentID=d2.studentID
     ) as d
WHERE a.studentID = t.ID and b.studentID = t.ID and c.studentID = t.ID and d.studentID = t.ID


# 使用分窗函数，难点依然是只有一条IO怎么办
#

SELECT t.ID, (ins-oos)/3600 , dateDiff((firstIsIn||(isnull(c.firstIsIn)))*(curdate()-365), (lastIsOut||(isnull(lastIsOut)))*curdate())
FROM (
         SELECT studentID, SUM(IOTime) as ins
         FROM IOLog
         WHERE datediff(curdate(),date(IOtime)) < 365 AND IOType='in'
         GROUP BY studentID
     ) as a,
     (
         SELECT studentID, SUM(IOTime) as oos
         FROM IOLog
         WHERE datediff(curdate(),date(IOtime)) < 365  AND IOType='out'
         GROUP BY studentID
     ) as b,
     studentBelonging as t
        left join
     # c d 表只能确定至少有一条in和out的学生的记录
     # 只有一条in/out的学生怎么办呢
     # 只有in：in - year begin
     # 只有out：out - year end
     (
         # 考虑minIn不存在，即没有出校记录，这部分学生就不存在c1表里了
         SELECT c1.studentID, (TIMEDIFF(minInTime,minOutTime) < 0) as firstIsIn
         FROM (
                  SELECT studentID, MIN(IOTime) as minInTime
                  FROM IOLog
                  WHERE IOType='in'
                  GROUP BY studentID
              ) as c1,
              (
                  SELECT studentID, MIN(IOTime) as minOutTime
                  FROM IOLog
                  WHERE IOType='out'
                  GROUP BY studentID
              ) as c2
         WHERE c1.studentID=c2.studentID
     ) as c ON t.ID=c.studentID
         left join
     (
         # 考虑maxOut不存在，即没有入校记录，这部分学生就不存在d2表里了
         SELECT d1.studentID, (TIMEDIFF(maxInTime,maxOutTime) < 0) as lastIsOut
         FROM (
                  SELECT studentID, MAX(IOTime) as maxInTime
                  FROM IOLog
                  WHERE IOType='in'
                  GROUP BY studentID
              ) as d1,
              (
                  SELECT studentID, MIN(IOTime) as maxOutTime
                  FROM IOLog
                  WHERE IOType='out'
                  GROUP BY studentID
              ) as d2
         WHERE d1.studentID=d2.studentID
     ) as d ON t.ID=d.studentID
WHERE a.studentID = t.ID and b.studentID = t.ID




# 12.23 new
SELECT t.ID, IFNULL(ins, 0), IFNULL(oos, 0), (IFNULL(d.LastIsOut, 0))*current_date, (((IFNULL(d.LastIsOut, 0))*current_date+IFNULL(ins, 0))-(IFNULL(c.firstIsIn, 0))* (current_date-365)+IFNULL(oos, 0)) as firstLast
FROM  studentBelonging as t
          left join
      (
          SELECT studentID, IFNULL(SUM(date(IOTime)), 0) as ins
          FROM IOLog
          WHERE datediff(curdate(),date(IOtime)) < 365 AND IOType='in'
          GROUP BY studentID
      ) as a on t.ID=a.studentID
          left join
      (
          SELECT studentID, IFNULL(SUM(date(IOTime)), 0) as oos
          FROM IOLog
          WHERE datediff(curdate(),date(IOtime)) < 365  AND IOType='out'
          GROUP BY studentID
      ) as b on t.ID=b.studentID
          left join
      (
          # 考虑minIn不存在，即没有出校记录，这部分学生就不存在c1表里了
          SELECT c1.studentID, (TIMEDIFF(minInTime,minOutTime) < 0) as firstIsIn
          FROM (
                   SELECT studentID, MIN(IOTime) as minInTime
                   FROM IOLog
                   WHERE IOType='in'
                   GROUP BY studentID
               ) as c1,
               (
                   SELECT studentID, MIN(IOTime) as minOutTime
                   FROM IOLog
                   WHERE IOType='out'
                   GROUP BY studentID
               ) as c2
          WHERE c1.studentID=c2.studentID
      ) as c ON t.ID=c.studentID
          left join
      (
          # 考虑maxOut不存在，即没有入校记录，这部分学生就不存在d2表里了
          SELECT d1.studentID, (TIMEDIFF(maxInTime,maxOutTime) < 0) as LastIsOut
          FROM (
                   SELECT studentID, MAX(IOTime) as maxInTime
                   FROM IOLog
                   WHERE IOType='in'
                   GROUP BY studentID
               ) as d1,
               (
                   SELECT studentID, MIN(IOTime) as maxOutTime
                   FROM IOLog
                   WHERE IOType='out'
                   GROUP BY studentID
               ) as d2
          WHERE d1.studentID=d2.studentID
      ) as d ON t.ID=d.studentID



