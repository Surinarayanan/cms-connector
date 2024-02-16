package com.p3.content.export.handler;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import java.io.IOException;

public class PdfExportHandler implements IEventHandler {
    PdfXObject stationery;
    public PdfExportHandler(PdfDocument pdf, PdfReader tem) throws IOException {
        PdfDocument template = new PdfDocument(tem);
        PdfPage page = template.getFirstPage();
        stationery = page.copyAsFormXObject(pdf);
        template.close();
    }

    public PdfPage getPage(PdfReader tem) throws IOException {
        PdfDocument template = new PdfDocument(tem);
        PdfPage page = template.getFirstPage();
        template.close();
        return page;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        PdfCanvas pdfCanvas = new PdfCanvas(
                page.newContentStreamBefore(), page.getResources(), pdf);
        pdfCanvas.addXObject(stationery);
    }
}
