package com.deepdefender.finalyearproject;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class PDFUtil {

    public static String generate(Context context,
                                  String month,
                                  int veg, int kirana, int gas,
                                  int workers, int total) {

        try {
            File dir = new File(
                    context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                    "MessBills"
            );
            if (!dir.exists()) dir.mkdirs();

            String safeMonth = month.replace(" ", "_");
            File file = new File(dir, "Mess_Bill_" + safeMonth + ".pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            document.add(new Paragraph("MESS MONTHLY BILL\n\n"));
            document.add(new Paragraph("Month: " + month));
            document.add(new Paragraph("Vegetables: ₹" + veg));
            document.add(new Paragraph("Kirana: ₹" + kirana));
            document.add(new Paragraph("Gas: ₹" + gas));
            document.add(new Paragraph("Workers: ₹" + workers));
            document.add(new Paragraph("\nTOTAL: ₹" + total));

            document.close();

            return "MessBills/" + file.getName(); // 👈 RETURN PATH

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
