class JobStat(val start: Int,val stop: Int,val count: Int,val time: Long) {

    override fun toString(): String {
        return "(start:${start},end:${stop},count:$count,time:$time)"
    }
}