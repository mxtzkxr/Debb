import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DBHelper(
    val dbName: String,
    val host: String = "localhost",
    val port: Int = 3306,
    val username: String = "root",
    val password: String = "root"
) {
    val conn: Connection
    init{
        try {
            Class.forName("com.mysql.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            println("Unable to load class.")
            e.printStackTrace()
        }
        conn = DriverManager.getConnection(
            "jdbc:mysql://$host:$port/$dbName?serverTimezone=UTC",
            username, password
        )
    }

    fun createTables(){
        val statement = conn.createStatement()
        statement.execute("CREATE TABLE IF NOT EXISTS `a` (" +
                "id INT NOT NULL AUTO_INCREMENT, " +
                "text VARCHAR(30) NOT NULL, " +
                "PRIMARY KEY(`id`)" +
                ")")
    }

    fun insertInto(dbName: String, tableName: String, fields: String, values: String){
        try{
            conn.createStatement().apply {
                execute("USE $dbName")
                execute("INSERT INTO `$tableName` ($fields) VALUES ($values)")
            }
        }catch (ex: SQLException){
            println("Не удалось добавить запись")
        }
    }

    /*fun load(filename: String){
        val csvSplitter = ","
        var line = ""
        try{
        val br = BufferedReader(
            InputStreamReader(
            FileInputStream(filename), "UTF-8"))
            while (br.readLine().also { line = it } != null) {
                val c: List<String> = line.split(csvSplitter)
            }

        }catch (e: Exception){
            println("Ошибка при чтении из файла: ${e.message}")
        }
    }*/
    fun createDataBaseFromDump(path: String){
        println("Создание структуры базы данных из дампа...")
        try {
            val s = connection?.createStatement()
            var query = ""
            File(path).forEachLine {
                if(!it.startsWith("--") && it.isNotEmpty()){
                    query += it;
                    if (it.endsWith(';')) {
                        s?.addBatch(query)
                        query = ""
                    }
                }
            }
            s?.executeBatch()
            println("Структура базы данных успешно создана.")
        }catch (e: SQLException){ println(e.message)
        }catch (e: Exception){ println(e.message)}
    }
    fun Stipend(){
        println("Стипендии по результатам последней сессии")
        val s = connection?.createStatement()
        val rs = s?.executeQuery(
                "SELECT\n" +
                        "full_name,\n" +
                        "group_id,\n" +
                        "CASE\n" +
                        "WHEN min_score >= 86 AND max_attempt = 1\n" +
                        "THEN 'повышенная'\n" +
                        "WHEN min_score >= 71 AND min_score < 86 AND max_attempt = 1\n" +
                        "THEN 'обычная'\n" +
                        "ELSE 'а нету ничего, дааа'\n" +
                        "END AS stipend\n" +
                        "\n" +
                        "FROM(SELECT\n" +
                        "stud_id,\n" +
                        "full_name,\n" +
                        "group_id,\n" +
                        "previous_semester,\n" +
                        "MIN(score) AS min_score,\n" +
                        "MAX(score) AS max_score,\n" +
                        "MAX(attempt) AS max_attempt,\n" +
                        "COUNT(discipline) AS discipline_count\n" +
                        "FROM(\n" +
                        "SELECT\n" +
                        "stud_id,\n" +
                        "full_name,\n" +
                        "gr_id AS group_id,\n" +
                        "previous_semester,\n" +
                        "disciplines.name AS discipline,\n" +
                        "disciplines_plans.reporting_form AS reporting,\n" +
                        "performance.score AS score,\n" +
                        "performance.attempt AS attempt\n" +
                        "FROM (SELECT\n" +
                        "students.id AS stud_id,\n" +
                        "CONCAT(last_name, ' ',SUBSTR(first_name, 1, 1), '.',\n" +
                        "SUBSTR(mid_name, 1, 1),\n" +
                        "IF(mid_name IS NULL OR TRIM(mid_name) = '', '', '.')) AS full_name, — Вывод инициалов\n" +
                        "semester.previous_semester AS previous_semester,\n" +
                        "gr_id\n" +
                        "FROM students INNER JOIN (SELECT year,\n" +
                        "groups.id AS gr_id,\n" +
                        "2*(YEAR(NOW()) - year) - (CASE WHEN MONTH(NOW()) < 6 OR MONTH(NOW()) = 1 THEN 1 WHEN MONTH(NOW()) = 1 THEN 2 ELSE 0 END) AS `previous_semester`\n" +
                        "FROM `groups` INNER JOIN `academic_plans`\n" +
                        "ON `groups`.`academic_plan_id` = `academic_plans`.`id`) AS semester\n" +
                        "ON students.group_id = semester.gr_id)\n" +
                        "AS full_stud\n" +
                        "\n" +
                        "LEFT JOIN performance ON stud_id = performance.student_id\n" +
                        "\n" +
                        "LEFT JOIN disciplines_plans ON\n" +
                        "performance.disciplines_plan_id = disciplines_plans.id AND\n" +
                        "disciplines_plans.semester_number = previous_semester\n" +
                        "\n" +
                        "LEFT JOIN disciplines ON disciplines_plans.discipline_id = disciplines.id\n" +
                        "WHERE TRIM(disciplines_plans.reporting_form) = 'экзамен'\n" +
                        "ORDER BY group_id, full_name\n" +
                        ") AS info\n" +
                        "GROUP BY stud_id, full_name, group_id, previous_semester) AS stp"

            )
        }


    }

}
