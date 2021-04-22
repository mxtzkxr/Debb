import org.apache.poi.xssf.usermodel.XSSFWorkbook

fun main(){
    val dbh = DBHelper("ded")
    dbh.createDataBaseFromDump("src/students.sql")
    //val path = "C:\\Users\\WhoAmI\\IdeaProjects\\Deb\\src\\05-803.xlsx"
    //val wb = XSSFWorkbook(path)
    //readTablesAndInsert(wb, dbh, "ded")
}

fun readTablesAndInsert(wb: XSSFWorkbook, dbh: DBHelper, dbName: String){
    wb.sheetIterator().forEach {
        val rSize = it.physicalNumberOfRows
        val cSize = it.getRow(0).physicalNumberOfCells
        val tabName = it.sheetName
        for(i in 1 until rSize){
            val fields = StringBuilder()
            val data = StringBuilder()
            val r = it.getRow(i)
            if(r == null){
                break
            }else{
                for(j in 0 until cSize){
                    val f = it.getRow(0).getCell(j)
                    val c = r.getCell(j)
                    if(c == null){
                        break
                    }else{
                        fields.append(" `${f}`,")
                        data.append(" \'${c}\',")
                    }
                }
            }
            val f: String = fields.toString().trim().split(",").toMutableList().apply { removeIf { that->that.isBlank() } }.joinToString(",")
            val d: String = data.toString().trim().split(",").toMutableList().apply { removeIf { that->that.isBlank() } }.joinToString(",")
            dbh.insertInto(dbName, tabName, f, d)
        }
    }
}