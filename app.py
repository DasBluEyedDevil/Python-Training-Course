"""
Python Learning Platform - Main Application
Built with Flask 3.0+ for Python 3.12+
An interactive, comprehensive course from beginner to full-stack developer
"""

from flask import Flask, render_template, jsonify, request, send_from_directory
import json
import os
from pathlib import Path

app = Flask(__name__)
app.config['SECRET_KEY'] = 'dev-secret-key-change-in-production'

# Path configurations
CONTENT_DIR = Path(__file__).parent / 'content'
MODULES_DIR = CONTENT_DIR / 'modules'
QUIZZES_DIR = CONTENT_DIR / 'quizzes'

# Course structure
COURSE_STRUCTURE = {
    "title": "Python: From Zero to Full-Stack Developer",
    "version": "1.0.0",
    "modules": [
        {
            "id": 1,
            "title": "The Absolute Basics",
            "subtitle": "The 'What'",
            "lessons": 5,
            "icon": "🎯"
        },
        {
            "id": 2,
            "title": "Storing & Using Information",
            "subtitle": "The 'Boxes'",
            "lessons": 5,
            "icon": "📦"
        },
        {
            "id": 3,
            "title": "Making Decisions",
            "subtitle": "The 'Forks in the Road'",
            "lessons": 6,
            "icon": "🔀"
        },
        {
            "id": 4,
            "title": "Repeating Actions",
            "subtitle": "The 'Loops'",
            "lessons": 5,
            "icon": "🔁"
        },
        {
            "id": 5,
            "title": "Grouping Information",
            "subtitle": "The 'Containers'",
            "lessons": 6,
            "icon": "🗂️"
        },
        {
            "id": 6,
            "title": "Creating Reusable Tools",
            "subtitle": "The 'Recipes'",
            "lessons": 6,
            "icon": "🧰"
        },
        {
            "id": 7,
            "title": "Handling Mistakes",
            "subtitle": "The 'Safety Nets'",
            "lessons": 6,
            "icon": "🛡️"
        },
        {
            "id": 8,
            "title": "Blueprints for Code",
            "subtitle": "Object-Oriented Programming",
            "lessons": 6,
            "icon": "🏗️"
        },
        {
            "id": 9,
            "title": "Working with the Real World",
            "subtitle": "Files & Libraries",
            "lessons": 6,
            "icon": "🌍"
        },
        {
            "id": 10,
            "title": "Building for the Web",
            "subtitle": "Back-End",
            "lessons": 6,
            "icon": "🌐"
        },
        {
            "id": 11,
            "title": "Storing Data",
            "subtitle": "Databases",
            "lessons": 6,
            "icon": "💾"
        },
        {
            "id": 12,
            "title": "Building for the User",
            "subtitle": "Front-End Basics",
            "lessons": 6,
            "icon": "🎨"
        },
        {
            "id": 13,
            "title": "Tying It All Together",
            "subtitle": "Full Stack",
            "lessons": 6,
            "icon": "🚀"
        },
        {
            "id": 14,
            "title": "Sharing Your Work",
            "subtitle": "Deployment & Tools",
            "lessons": 5,
            "icon": "📤"
        }
    ]
}


@app.route('/')
def index():
    """Landing page with course overview"""
    return render_template('index.html', course=COURSE_STRUCTURE)


@app.route('/module/<int:module_id>')
def module_view(module_id):
    """View a specific module with its lessons"""
    if module_id < 1 or module_id > len(COURSE_STRUCTURE['modules']):
        return "Module not found", 404

    module = COURSE_STRUCTURE['modules'][module_id - 1]
    return render_template('module.html', module=module, course=COURSE_STRUCTURE)


@app.route('/lesson/<int:module_id>/<int:lesson_id>')
def lesson_view(module_id, lesson_id):
    """View a specific lesson"""
    try:
        lesson_file = MODULES_DIR / f'module_{module_id:02d}' / f'lesson_{lesson_id:02d}.json'

        if not lesson_file.exists():
            return "Lesson not found", 404

        with open(lesson_file, 'r', encoding='utf-8') as f:
            lesson_data = json.load(f)

        module = COURSE_STRUCTURE['modules'][module_id - 1]

        return render_template('lesson.html',
                             lesson=lesson_data,
                             module=module,
                             module_id=module_id,
                             lesson_id=lesson_id,
                             course=COURSE_STRUCTURE)
    except Exception as e:
        return f"Error loading lesson: {str(e)}", 500


@app.route('/quiz/<int:module_id>')
def quiz_view(module_id):
    """View a module quiz"""
    try:
        quiz_file = QUIZZES_DIR / f'quiz_{module_id:02d}.json'

        if not quiz_file.exists():
            return "Quiz not found", 404

        with open(quiz_file, 'r', encoding='utf-8') as f:
            quiz_data = json.load(f)

        module = COURSE_STRUCTURE['modules'][module_id - 1]

        return render_template('quiz.html',
                             quiz=quiz_data,
                             module=module,
                             module_id=module_id,
                             course=COURSE_STRUCTURE)
    except Exception as e:
        return f"Error loading quiz: {str(e)}", 500


@app.route('/api/course-structure')
def api_course_structure():
    """API endpoint for course structure"""
    return jsonify(COURSE_STRUCTURE)


@app.route('/api/lesson/<int:module_id>/<int:lesson_id>')
def api_lesson(module_id, lesson_id):
    """API endpoint for lesson data"""
    try:
        lesson_file = MODULES_DIR / f'module_{module_id:02d}' / f'lesson_{lesson_id:02d}.json'

        if not lesson_file.exists():
            return jsonify({"error": "Lesson not found"}), 404

        with open(lesson_file, 'r', encoding='utf-8') as f:
            lesson_data = json.load(f)

        return jsonify(lesson_data)
    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/progress', methods=['GET', 'POST'])
def api_progress():
    """API endpoint for progress tracking (client-side with localStorage)"""
    if request.method == 'POST':
        # In a production app, this would save to a database
        # For now, we rely on client-side localStorage
        return jsonify({"status": "success"})
    else:
        return jsonify({"message": "Progress is tracked client-side"})


@app.route('/playground')
def playground():
    """Interactive Python code playground"""
    return render_template('playground.html')


if __name__ == '__main__':
    # Create necessary directories
    MODULES_DIR.mkdir(parents=True, exist_ok=True)
    QUIZZES_DIR.mkdir(parents=True, exist_ok=True)

    print("🚀 Starting Python Learning Platform")
    print("📚 Course: From Zero to Full-Stack Developer")
    print(f"🐍 Python 3.14+ | Flask 3.1.2")
    print("🌐 Visit: http://localhost:5000")
    print("-" * 50)

    app.run(debug=True, host='0.0.0.0', port=5000)
