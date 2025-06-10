package logic.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;

public class AssignmentPDFGenerator {

    private static final Font FONT_NORMAL = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL, BaseColor.BLACK);
    private static final Font FONT_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD, BaseColor.BLACK);
    private static final Font FONT_HEADER = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);

    public static void generatePDF(String outputPath, AssignmentData data) throws IOException, DocumentException {
        Document document = new Document(PageSize.LETTER, 40, 40, 40, 40);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            Paragraph header = new Paragraph();
            header.add(new Chunk("Facultad de Estadística e Informática\n", FONT_HEADER));
            header.add(new Chunk("Dirección\nAv. Xalapa esq. Ávila Camacho\nS/N\nCol. Obrero Campesina\nCP 91020\nXalapa de Enríquez\nVeracruz, México\n\n", FONT_NORMAL));
            document.add(header);

            Paragraph date = new Paragraph("Xalapa-Enríquez, Veracruz, a " + data.getFormattedCurrentDate() + "\n\n", FONT_NORMAL);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);

            Paragraph recipient = new Paragraph();
            recipient.add(new Chunk((data.getRepresentativeFirstName() + " " + data.getRepresentativeLastName()).toUpperCase() + "\n", FONT_BOLD));
            recipient.add(new Chunk(data.getOrganizationName().toUpperCase() + "\n", FONT_BOLD));
            recipient.add(new Chunk(data.getOrganizationAddress().toUpperCase() + "\n\n", FONT_BOLD));
            document.add(recipient);

            Paragraph body = new Paragraph();
            body.add(new Chunk("En atención a su solicitud expresada a la Coordinación de Prácticas Profesionales de la\n", FONT_NORMAL));
            body.add(new Chunk("Licenciatura en Ingeniería de Software, hacemos de su conocimiento que el C. ", FONT_NORMAL));
            body.add(new Chunk((data.getStudentFirstName() + " " + data.getStudentLastName()).toUpperCase(), FONT_BOLD));
            body.add(new Chunk(", estudiante de la Licenciatura con matrícula ", FONT_NORMAL));
            body.add(new Chunk(data.getStudentTuition(), FONT_BOLD));
            body.add(new Chunk(", ha sido asignado al\nproyecto de ", FONT_NORMAL));
            body.add(new Chunk(data.getProjectName().toUpperCase(), FONT_BOLD));
            body.add(new Chunk(", a su digno cargo a partir del ", FONT_NORMAL));
            body.add(new Chunk(data.getCurrentDayMonth().toUpperCase(), FONT_BOLD));
           body.add(new Chunk(" del presente hasta cubrir ", FONT_NORMAL));
            body.add(new Chunk("420 HORAS", FONT_BOLD));
            body.add(new Chunk(". Cabe mencionar que el estudiante cuenta con la formación y el\n", FONT_NORMAL));
            body.add(new Chunk("perfil para las actividades a desempeñar.\n\n", FONT_NORMAL));


            body.add(new Paragraph(
                    "Anexo a este documento usted encontrará una copia del horario de las experiencias educativas que el estudiante asignado se encuentra cursando para que sea respetado y tomado en cuenta al momento de establecer el horario de realización de sus Prácticas Profesionales. Por otra parte, le solicito de la manera más atenta, haga llegar a la brevedad con el estudiante, el oficio de aceptación así como el plan de trabajo detallado del estudiante, además el horario que cubrirá. Deberá indicar además, la forma en que se registrará la evidencia de asistencia y número de horas cubiertas. Es importante mencionar que el estudiante deberá presentar mensualmente un reporte de avances de sus prácticas. Este reporte de avances puede entregarse hasta con una semana de atraso por lo que le solicito de la manera más atenta sean elaborados y avalados (incluyendo sello si aplica) de manera oportuna para su entrega al académico responsable de la experiencia de Prácticas de Ingeniería de Software. En relación con lo anterior, es importante que en el oficio de aceptación proporcione el nombre de la persona que supervisará y avalará en su dependencia la prestación de las prácticas profesionales así como número telefónico, extensión (cuando aplique) y correo electrónico. Lo anterior con el fin de contar con el canal de comunicación que permita dar seguimiento al desempeño del estudiante. \n" +
                            "Le informo que las Prácticas de Ingeniería de Software forman parte de la currícula de la Licenciatura en Ingeniería de Software, por lo cual es necesaria su evaluación y de ahí la necesidad de realizar el seguimiento correspondiente. Es por ello que, durante el semestre, el coordinador de Prácticas de Ingeniería de Software realizará al menos un seguimiento de las actividades del estudiante por lo que será necesario mostrar evidencias de la asistencia del estudiante, así como de sus actividades. Este seguimiento podrá ser vía correo electrónico, teléfono o incluso mediante una visita a sus oficinas, por lo que le solicito de la manera más atenta, proporcione las facilidades requeridas en su caso. \n" +
                            "Sin más por el momento, agradezco su atención al presente reiterándome a sus apreciables órdenes.",
                    FONT_NORMAL
            ));
            document.add(body);
            Paragraph signature = new Paragraph("\n\nAtentamente\n\n\n\n", FONT_NORMAL);
            signature.add(new Chunk("Dr. Ángel Juan Sánchez García\n", FONT_BOLD));
            signature.add(new Chunk("Coordinador de Servicio Social y Prácticas Profesionales", FONT_NORMAL));
            document.add(signature);

        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}