package com.quickref.plugin.action.xml

import org.w3c.dom.Document
import org.w3c.dom.Node
import java.awt.Color
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object ModifyColorsXmlDomParser {
    @Suppress("MagicNumber")
    private val defaultMask = Color(0.0f, 0.0f, 0.0f, 0.25f)

    fun generatorXML(file: File) {
        val dbf = DocumentBuilderFactory.newInstance()
        try {
            FileInputStream(file).use { fis ->
                val db = dbf.newDocumentBuilder()
                val doc = db.parse(fis)
                val colors = doc.getElementsByTagName("color")
                for (index in 0 until colors.length) {
                    // get first staff
                    val colorNode = colors.item(index)
                    if (colorNode.nodeType == Node.ELEMENT_NODE) {
                        println(colorNode.textContent)
                        val colorValue = colorNode.textContent
                        val newColor = mixColors(Color.decode(colorValue), defaultMask)
                        val newColorString = formatColor(newColor.rgb)
                        colorNode.textContent = newColorString
                    }
                }

                FileOutputStream(file).use { output ->
                    writeXml(doc, output)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @Suppress("MagicNumber")
    private fun formatColor(color: Int): String {
        return java.lang.String.format("#%06X", 0xFFFFFF and color)
    }

    @Suppress("MagicNumber")
    private fun mixColors(color: Color, mask: Color): Color {
        val ratio = (255 - mask.alpha) / 255.0f
        val r = color.red * ratio / 255.0f
        val g = color.green * ratio / 255.0f
        val b = color.blue * ratio / 255.0f
        val a = 1.0f
        return Color(r, g, b, a)
    }

    // write doc to output stream
    @Throws(TransformerException::class, UnsupportedEncodingException::class)
    private fun writeXml(
        doc: Document,
        output: OutputStream,
    ) {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        // pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no")
        val source = DOMSource(doc)
        val result = StreamResult(output)
        transformer.transform(source, result)
    }
}
