/**
 * Python Learning Platform - Code Editor
 * Handles code execution and editor functionality
 */

class PythonCodeRunner {
    constructor() {
        this.outputElement = null;
        this.isRunning = false;
    }

    /**
     * Simulate Python code execution
     * In production, this would use Pyodide (Python in WebAssembly)
     * or a secure server-side execution environment
     */
    async runCode(code, outputElement) {
        this.outputElement = outputElement;
        this.isRunning = true;

        try {
            // Show loading state
            this.showOutput('Running code...\n', 'info');

            // Simulate execution delay
            await this.delay(500);

            // Parse and simulate basic Python operations
            const result = this.simulateExecution(code);

            this.showOutput(result, 'success');
        } catch (error) {
            this.showOutput(`Error: ${error.message}`, 'error');
        } finally {
            this.isRunning = false;
        }
    }

    /**
     * Simulate basic Python execution
     * This is a demonstration version - not a real Python interpreter
     */
    simulateExecution(code) {
        let output = '';

        try {
            // Extract print statements
            const printRegex = /print\((.*?)\)/g;
            const prints = [...code.matchAll(printRegex)];

            if (prints.length > 0) {
                output += '=== Output ===\n';
                prints.forEach(match => {
                    const content = match[1].trim();
                    // Remove quotes if present
                    const cleaned = content.replace(/^['"]|['"]$/g, '');
                    output += `${cleaned}\n`;
                });
            }

            // Detect variable assignments
            const varRegex = /(\w+)\s*=\s*(.+)/g;
            const variables = [...code.matchAll(varRegex)];

            if (variables.length > 0) {
                output += '\n=== Variables ===\n';
                variables.forEach(match => {
                    const varName = match[1];
                    const varValue = match[2].trim();
                    output += `${varName} = ${varValue}\n`;
                });
            }

            // Detect function definitions
            const funcRegex = /def\s+(\w+)\s*\(/g;
            const functions = [...code.matchAll(funcRegex)];

            if (functions.length > 0) {
                output += '\n=== Functions Defined ===\n';
                functions.forEach(match => {
                    output += `✓ ${match[1]}()\n`;
                });
            }

            // Detect class definitions
            const classRegex = /class\s+(\w+)/g;
            const classes = [...code.matchAll(classRegex)];

            if (classes.length > 0) {
                output += '\n=== Classes Defined ===\n';
                classes.forEach(match => {
                    output += `✓ ${match[1]}\n`;
                });
            }

            if (output === '') {
                output = '⚠️ No output to display.\n\n';
                output += '💡 Note: This is a simulated environment.\n';
                output += 'In a production version, this would use:\n';
                output += '  • Pyodide (Python in WebAssembly for browser execution)\n';
                output += '  • Or a secure server-side Python execution API\n';
            } else {
                output += '\n✓ Code execution simulated successfully!\n';
                output += '\n💡 For real Python execution:\n';
                output += '  • Install Python locally\n';
                output += '  • Or use an online Python REPL like replit.com or python.org/shell\n';
            }

            return output;
        } catch (error) {
            throw new Error(`Execution failed: ${error.message}`);
        }
    }

    /**
     * Show output in the output element
     */
    showOutput(text, type = 'normal') {
        if (!this.outputElement) return;

        const container = document.getElementById('output-container');
        if (container) {
            container.style.display = 'block';
        }

        // Color-code based on type
        let className = '';
        switch (type) {
            case 'error':
                className = 'output-error';
                break;
            case 'info':
                className = 'output-info';
                break;
            case 'success':
                className = 'output-success';
                break;
        }

        this.outputElement.textContent = text;
        this.outputElement.className = `code-output ${className}`;
    }

    /**
     * Clear output
     */
    clearOutput() {
        if (this.outputElement) {
            this.outputElement.textContent = '';
            const container = document.getElementById('output-container');
            if (container) {
                container.style.display = 'none';
            }
        }
    }

    /**
     * Utility: delay function
     */
    delay(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    /**
     * Validate Python syntax (basic)
     */
    validateSyntax(code) {
        const errors = [];

        // Check for basic syntax issues
        const lines = code.split('\n');

        lines.forEach((line, index) => {
            // Check for unclosed parentheses
            const openParens = (line.match(/\(/g) || []).length;
            const closeParens = (line.match(/\)/g) || []).length;

            if (openParens !== closeParens) {
                errors.push(`Line ${index + 1}: Mismatched parentheses`);
            }

            // Check for unclosed quotes
            const quotes = (line.match(/"/g) || []).length;
            if (quotes % 2 !== 0) {
                errors.push(`Line ${index + 1}: Unclosed quote`);
            }
        });

        return {
            valid: errors.length === 0,
            errors: errors
        };
    }
}

// ============================================================================
// Code Editor Setup
// ============================================================================
document.addEventListener('DOMContentLoaded', () => {
    const runButton = document.getElementById('run-code');
    const clearOutputBtn = document.getElementById('clear-output');

    if (runButton) {
        const codeRunner = new PythonCodeRunner();

        runButton.addEventListener('click', async () => {
            const editor = document.querySelector('.CodeMirror');
            if (!editor) return;

            const code = editor.CodeMirror.getValue();
            const outputElement = document.getElementById('code-output');

            if (!code.trim()) {
                codeRunner.showOutput('⚠️ No code to run. Write some Python code first!', 'info');
                return;
            }

            // Validate syntax
            const validation = codeRunner.validateSyntax(code);
            if (!validation.valid) {
                codeRunner.showOutput(
                    `Syntax Errors:\n${validation.errors.join('\n')}`,
                    'error'
                );
                return;
            }

            // Run the code
            await codeRunner.runCode(code, outputElement);
        });

        if (clearOutputBtn) {
            clearOutputBtn.addEventListener('click', () => {
                codeRunner.clearOutput();
            });
        }
    }
});

// ============================================================================
// Code Formatting Utilities
// ============================================================================
class CodeFormatter {
    /**
     * Format Python code (basic indentation)
     */
    static format(code) {
        const lines = code.split('\n');
        let indentLevel = 0;
        const indentSize = 4;
        const formattedLines = [];

        lines.forEach(line => {
            const trimmed = line.trim();

            // Decrease indent for lines starting with return, break, continue, pass
            if (/^(return|break|continue|pass)\b/.test(trimmed)) {
                // Keep current indent
            }
            // Decrease indent for lines starting with else, elif, except, finally
            else if (/^(else|elif|except|finally):/.test(trimmed)) {
                indentLevel = Math.max(0, indentLevel - 1);
            }

            // Add the line with proper indentation
            if (trimmed) {
                formattedLines.push(' '.repeat(indentLevel * indentSize) + trimmed);
            } else {
                formattedLines.push('');
            }

            // Increase indent for lines ending with colon
            if (trimmed.endsWith(':')) {
                indentLevel++;
            }
            // Decrease indent after return, break, continue, pass
            else if (/^(return|break|continue|pass)\b/.test(trimmed)) {
                indentLevel = Math.max(0, indentLevel - 1);
            }
        });

        return formattedLines.join('\n');
    }

    /**
     * Add syntax highlighting to code blocks
     */
    static highlightSyntax(code) {
        // This is a simple highlighter - in production, use a library like Prism.js
        const keywords = [
            'def', 'class', 'if', 'elif', 'else', 'for', 'while', 'return',
            'import', 'from', 'as', 'try', 'except', 'finally', 'with',
            'lambda', 'yield', 'break', 'continue', 'pass', 'raise',
            'True', 'False', 'None', 'and', 'or', 'not', 'in', 'is'
        ];

        let highlighted = code;

        keywords.forEach(keyword => {
            const regex = new RegExp(`\\b${keyword}\\b`, 'g');
            highlighted = highlighted.replace(regex, `<span class="keyword">${keyword}</span>`);
        });

        return highlighted;
    }
}

// ============================================================================
// Code Snippets Library
// ============================================================================
class CodeSnippets {
    static snippets = {
        'hello': 'print("Hello, World!")',

        'variable': `name = "Python"
version = 3.14
print(f"Language: {name}, Version: {version}")`,

        'function': `def greet(name):
    return f"Hello, {name}!"

message = greet("World")
print(message)`,

        'loop': `for i in range(5):
    print(i)`,

        'list': `fruits = ["apple", "banana", "orange"]
for fruit in fruits:
    print(fruit)`,

        'class': `class Dog:
    def __init__(self, name):
        self.name = name

    def bark(self):
        return f"{self.name} says Woof!"

my_dog = Dog("Buddy")
print(my_dog.bark())`
    };

    /**
     * Get a code snippet by name
     */
    static get(name) {
        return this.snippets[name] || '';
    }

    /**
     * List all available snippets
     */
    static list() {
        return Object.keys(this.snippets);
    }
}

// Make classes available globally
window.PythonCodeRunner = PythonCodeRunner;
window.CodeFormatter = CodeFormatter;
window.CodeSnippets = CodeSnippets;
