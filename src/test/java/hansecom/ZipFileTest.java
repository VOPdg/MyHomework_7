package hansecom;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipFileTest {
    @Test
    public void unzipFile() throws Exception {
        ZipFile zipFile = new ZipFile("src/test/resources/Archive.zip");
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                System.out.println("dir  : " + entry.getName());
            } else {
                System.out.println("file : " + entry.getName());
            }
            if (entry.getName().equals("20210903_HHA_LD Tickets (1).csv")) {
                InputStream inputStream = zipFile.getInputStream(entry);
                CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
                List<String[]> list = reader.readAll();
                assertThat(list)
                        .hasSize(46)
                        .contains(new String[]{
                                "KP;943;Preview",
                                " Back Buttond and Accordeon funtionality"
                        }, Index.atIndex(5)); // проверили содержание 6 строки
            }
            if (entry.getName().equals("20210903_HHA_LD Tickets (1).xlsx")) {
                InputStream inputStream = zipFile.getInputStream(entry);
                XLS xlsParsed = new XLS(inputStream);
                assertThat(xlsParsed.excel.getSheetAt(0).getRow(3).getCell(2).getStringCellValue()) //ошибка такая же как и у Дмитрия на 1.19.52
                        .isEqualTo("Bread Crums Adjustment");
            }
            if (entry.getName().equals("2019011510421217.pdf")) {
                InputStream inputStream = zipFile.getInputStream(entry);
                PDF pdfParsed = new PDF(inputStream);
                assertThat(pdfParsed.text).contains("Отсутствие характерных признаков приводит к поздней диагностике миксомы сердца");
            }
        }
    }
}
