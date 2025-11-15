from pypdf import PdfReader
import sys

pdf_path = "Enunciado TPI - 2025.pdf"
reader = PdfReader(pdf_path)

text_parts = []
for page in reader.pages:
    text_parts.append(page.extract_text() or "")

full_text = "\n\n".join(text_parts)
print(full_text)

