package com.deepdefender.finalyearproject;


import android.content.Context;
import android.os.Environment;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class AttendancePdfUtil {


    public static File export(Context ctx, String date, List<AttendanceModel> list) throws Exception {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MessAttendance");
        if (!dir.exists()) dir.mkdirs();


        File file = new File(dir, "Attendance_" + date + "_"  + ".pdf");
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();


        Font title = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        doc.add(new Paragraph("Mess Attendance", title));
        doc.add(new Paragraph("Date: " + date));
        doc.add(new Paragraph(" "));


        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell("Name");
        table.addCell("Room");
        table.addCell("Status");
        table.addCell("Time");


        for (AttendanceModel m : list) {
            table.addCell(m.name);
            table.addCell(m.room);
            table.addCell(m.status);
            table.addCell(m.time);
        }


        doc.add(table);
        doc.close();
        return file;
    }
}