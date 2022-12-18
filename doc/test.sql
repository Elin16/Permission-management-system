select d.dptID, d.campusName from(
    select c.dptID, c.campusName, rank()over(partition by c.dptID order by (c.number) desc nulls last ) as io_rank
    from (
            select b.dptID, io.campusName, count(*) as number
            from studentBelonging as b, IOLog as io
            where b.ID=io.studentID
            group by b.dptID, io.campusName
        ) as c
) as d
where d.io_rank=1;