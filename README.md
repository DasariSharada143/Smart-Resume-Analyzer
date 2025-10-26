# Smart Resume Analyzer

**Version:** 3.0  
**Technology:** Java, Spark Framework, HTML/CSS/JavaScript  

---

## Overview

**Smart Resume Analyzer** is a web-based tool to automate the resume screening process. It allows recruiters to:

- Upload multiple resumes in PDF format.
- Automatically extract skills and other details from resumes.
- Match candidate skills with a job description.
- Select multiple candidates and compare them.
- Identify the best candidate based on skills and CGPA/score.

This tool saves time and helps recruiters make data-driven hiring decisions.

---

## Features

- Upload single or multiple resumes at once.
- Extract candidate details: Name, Email, Phone, Skills.
- Match skills with job description keywords.
- Suggest suitable roles for each candidate.
- Select multiple candidates and view their details.
- Highlight the **best candidate** based on skills and score.
- Flexible and responsive web interface.

---

## Project Structure
Smart_resume_analyser/
│
├─ src/ # Java source code
├─ bin/ # Compiled classes (ignored in GitHub)
├─ lib/ # External libraries (Spark, JSON)
├─ public/ # HTML, CSS, JS for web interface
├─ resumes/ # Sample job descriptions & resumes
├─ uploads/ # Uploaded resumes (ignored in GitHub)
├─ README.md # Project documentation
└─ .gitignore # Git ignore rules


---

## Installation & Setup

1. **Clone the repository**
```bash
git clone https://github.com/DasariSharada143/Smart-Resume-Analyzer.git
cd SmartResumeAnalyzer/Smart_resume_analyser
```
2. **Run the project**
```bash 
java -cp "bin;lib/*;C:\path\to\json-20210307.jar" WebApp
```

3. **Open your browser and go to:**
```bash
http://localhost:4567/index.html
```


