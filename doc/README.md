# Permission-management-system
author: Yixin Zhu, Yilin Huang

## Component
- README.md
- ER.dot: ER Model
- source: source code
- Description of the database table structure
- Index Definition Description
- Key SQL Operation Description
- Storage
- Trigger
### ER
### Entity
- student
- admin
- super_user
- apartment
- class
- tutor
- campus
- permission
- entry_log
- health_report
- leave_registration
- return_application
### Relation
pass
## Query 
student, class, department = studnetBelonging
| need | table | sql |
| --   | --    | --  |
|  学生过去 n 天的每日填报信息   |  healthReport, scd     |     |
|  学生的入校权限  |  scd    |     |
|  查询学生的入校申请、出校申请，支持按状态（待审核、已同意、已拒绝）进行筛选   |  entryApplication, leaveApplication, scd|     |
|  查询学生（从当天算起）过去一年的离校总时长    | IOLog, scd    |     |
|  过去 n 天尚未批准的入校申请和出校申请数量及详细信息    |  entryApplication, leaveApplication, scd  |     |
|  前 n 个提交入校申请最多的学生，支持按多级范围（全校、院系、班级）进行筛选    |   entryApplication, scd    |     |
|  前 n 个平均离校时间最长的学生，支持按多级范围（全校、院系、班级）进行筛选；    |  IOLog, scd     |     |
|  已出校但尚未返回校园（即离校状态）的学生数量、个人信息及各自的离校时间    |  IOLog, scd      |     |
|  未提交出校申请但离校状态超过 24h 的学生数量、个人信息    |  IOLog, scd      |     |
|  已提交出校申请但未离校的学生数量、个人信息    |  scd     |     |
|  过去 n 天一直在校未曾出校的学生，支持按多级范围（全校、院系、班级）进行筛选   |   IOLog, scd     |     |
|  连续 n 天填写“健康日报”时间（精确到分钟）完全一致的学生数量，个人信息   |  healthReport, scd     |     |
|  过去 n 天每个院系学生产生最多出入校记录的校区   | IOLog |     |

## 页面设计
### 登录页
学工号
密码
用户类型
有四种用户，student/tutor/admin/super admin
```
$ login -t <s/t/a/sa> -u <school number> -p
$ <password>
```

# student
## IO s
学生进出校记录生成（相当于打卡机）
进校/出校两个按钮，选择校区。有文本框显示是否成功进出。
学生在打卡进入或离开校区时，系统需要记录学生进入或离开的时间、对应的校区等信息。最新的打卡记录为离校的学生将被判定为离校状态，最近的打卡记录为在校的学生将被判定为在校状态。
```
$ i -c <campus name> -t timestamp
$ o -c <campus name> -t timestamp
```
## 健康日报打卡 s
学生当日健康状况、所在位置以及其他必要信息。
```
$ r
```
read from certain file.

### 申请页 s
```bash
$ e
$ l
```
学生出入校申请。
长时间（超过 1 天）离开学校，需要填写“离校报备表”，包括离校原因、目的地、预期离校日期、预期返回时间等。
填写“进校审批表”，包括进校原因、7 天内所到地区、预期进校日期等。

### 查询页 s t
健康日报/入校申请/出校申请/IO相关
基本查询需求
```bash
# 查询学生过去n天的每日填报信息
$ show-health-report -d <n days>
# 查询学生的入校权限
$ show-entry-perm 
#查询学生的入校申请、出校申请，支持按状态（待审核、已同意、已拒绝）进行筛选；
$ show-entry-app -s <wait/ack/ref>
$ show-leave-app -s <wait/ack/ref>
# 查询学生（从当天算起）过去一年的离校总时长
$ show-yearly-leave-time-count 
```
进阶查询
```bash
# r: range n: number s:state d: day oos: out of school
#  -u is (optional, means show upper layer statistics. default is show detail)
# 过去 n 天尚未批准的入校申请和出校申请数量及详细信息；可以按照申请状态筛选
$ show-entry-app -d <n days> (-u) -w <state>
$ show-leave-app -d <n days> (-u) -w <state>
# 前 n 个提交入校申请最多的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-most-entry-app -n <num student> -r <u/d/c> -id <null/dpt Id/class Id> 
# 前 n 个平均离校时间最长的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-most-leave-time -n <num student>  -r <u/d/c> -id <null/dpt Id/class Id> 
#  已出校但尚未返回校园（即离校状态）的学生数量、个人信息及各自的离校时间；
$ show-oos (-u)
# 未提交出校申请但离校状态超过 24h 的学生数量、个人信息；
$ show-stay-oos (-u)
# 已提交出校申请但未离校的学生数量、个人信息；
$ show-leave-is (-u)
# 过去 n 天一直在校未曾出校的学生，支持按多级范围（全校、院系、班级）进行筛选；
$ show-always-is -d <n days> -r <u/d/c> -id <null/dpt Id/class Id> 
# 连续 n 天填写“健康日报”时间（精确到分钟）完全一致的学生数量，个人信息；
$ show-exact-report -d <n days> (-u)
# 过去 n 天每个院系学生产生最多出入校记录的校区。
$ show-most-ios-campus
```
### 审批页 t
权限：TUTOR及ADMIN
```bash
$ OK <num> -r <reason>
$ NO <num> -r <reason>
$ OKALL
$ NOALL -r <reason>
```
