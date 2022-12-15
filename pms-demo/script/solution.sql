# 1 student
select *
from healthLog
where (studentID='19307090001' and datediff(curdate(), reportDate) < 5);

# 1 tutor
select *
from healthLog
where (studentID in(
    select studentID
    from student
    ) and datediff(curdate(), reportDate) < 5);

# 2 student
select entryPerm
from student
where ID='19307090001';















