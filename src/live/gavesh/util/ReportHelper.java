/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.gavesh.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import live.gavesh.data.Student;

public class ReportHelper {
    
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static final Font CELL_FONT = new Font(Font.FontFamily.HELVETICA, 11);
    
    private static final BaseColor HEADER_COLOR = new BaseColor(153, 0, 81);
    private static final float CELL_PADDING = 6f;
    private static final float HEADER_PADDING = 8f;
    
    private static ReportHelper instance = new ReportHelper();
    
    private ReportHelper() {
    }
    
    public void generateReport(String filePath, String title, List<String> headers, List<List<String>> rows)
            throws IOException, DocumentException {
        
        Document document = new Document(PageSize.A4, 50, 50, 70, 50); // more space at bottom for footer
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        
        writer.setPageEvent(new PdfPageEventHelper() {
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);
            
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                Phrase footer = new Phrase("Generated by Student Tracker Software - Page " + writer.getPageNumber(), footerFont);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        footer,
                        (document.right() - document.left()) / 2 + document.leftMargin(),
                        document.bottom() - 10, 0);
            }
        });
        
        document.open();
        
        String dateStr = java.time.LocalDate.now().toString();
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);
        Paragraph datePara = new Paragraph("Date: " + dateStr, dateFont);
        datePara.setAlignment(Element.ALIGN_RIGHT);
        document.add(datePara);
        
        Paragraph titlePara = new Paragraph(title, TITLE_FONT);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingBefore(10f);
        titlePara.setSpacingAfter(15f);
        document.add(titlePara);
        
        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, HEADER_FONT));
            headerCell.setBackgroundColor(HEADER_COLOR);
            headerCell.setPadding(HEADER_PADDING);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }
        
        for (List<String> row : rows) {
            for (String value : row) {
                PdfPCell cell = new PdfPCell(new Phrase(value, CELL_FONT));
                cell.setPadding(CELL_PADDING);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }
        }
        
        document.add(table);
        document.close();
    }
    
    public String compileStudentLowPerformanceReport() {
        String filePath = getDesktopPath() + "/StudentReport-" + getCurrentTimeStamp() + ".pdf";
        
        List<Student> students = DatabaseHelper.getStudentsWithLowPerformance(60);
        List<String> headers = List.of("ID", "Name", "Address");
        
        List<List<String>> rows = new ArrayList<>();
        for (Student student : students) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(student.getStudentId()));
            row.add(student.getFullName());
            row.add(student.getAddress());
            rows.add(row);
        }
        
        try {
            generateReport(filePath, "Low Performance Student Report", headers, rows);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating PDF report!");
            return null;
        }
        
        return filePath;
    }
    
    public String compileStudentAttendanceReport() {
        String filePath = getDesktopPath() + "/StudentAttendanceReport-" + getCurrentTimeStamp() + ".pdf";
        List<Student> students = DatabaseHelper.getAllStudents();
        List<String> headers = List.of("ID", "Name", "Attendance %");
        List<List<String>> rows = new ArrayList<>();
        for (Student student : students) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(student.getStudentId()));
            row.add(student.getFullName());
            double attendance = DatabaseHelper.getStudentAttendancePercentage(Integer.parseInt(student.getStudentId()));
            row.add(String.format("%.2f", attendance));
            rows.add(row);
        }
        try {
            generateReport(filePath, "Student Attendance Report", headers, rows);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating Student Attendance PDF report!");
            return null;
        }
        return filePath;
    }
    
    public String getCurrentTimeStamp() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return localDate.format(dateFormat);
    }
    
    public String getDesktopPath() {
        return System.getProperty("user.home") + "/Desktop";
    }
    
    public static ReportHelper getReportHandler() {
        return instance;
    }
    
}
