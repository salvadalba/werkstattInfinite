import type { PortfolioData } from './portfolio';

export const portfolioData: PortfolioData = {
    hero: {
        name: 'Your Name',
        title: 'Python Software Developer',
        subtitle: 'Building scalable web applications, data solutions, and automation tools',
        description: 'I specialize in Python development with expertise in full-stack web development, data science, and automation. I create efficient, scalable solutions that solve real-world problems.',
        ctaButtons: {
            primary: { text: 'View Projects', link: '#projects' },
            secondary: { text: 'Contact Me', link: '#contact' },
        },
        socialLinks: [
            { platform: 'GitHub', url: 'https://github.com/yourusername', icon: 'github' },
            { platform: 'LinkedIn', url: 'https://linkedin.com/in/yourusername', icon: 'linkedin' },
            { platform: 'Twitter', url: 'https://twitter.com/yourusername', icon: 'twitter' },
        ],
    },
    about: {
        bio: [
            'I am a passionate Python software developer with 5+ years of experience building web applications, data pipelines, and automation tools.',
            'My journey in programming started with a curiosity about how things work, which led me to discover the power and versatility of Python.',
            'I believe in writing clean, maintainable code and creating solutions that are not only functional but also efficient and scalable.',
            'When I\'m not coding, you can find me contributing to open-source projects, writing technical blog posts, or exploring the latest Python frameworks and tools.',
        ],
        experience: [
            {
                role: 'Senior Python Developer',
                company: 'Tech Company Inc.',
                period: '2022 - Present',
                description: [
                    'Led development of REST APIs using Django and FastAPI',
                    'Implemented data processing pipelines with Pandas and NumPy',
                    'Reduced data processing time by 60% through optimization',
                    'Mentored junior developers and conducted code reviews',
                ],
            },
            {
                role: 'Python Developer',
                company: 'Startup XYZ',
                period: '2020 - 2022',
                description: [
                    'Built web applications using Flask and PostgreSQL',
                    'Created automation scripts using Selenium and BeautifulSoup',
                    'Integrated third-party APIs for data collection',
                    'Improved application performance by 40%',
                ],
            },
            {
                role: 'Junior Developer',
                company: 'Agency ABC',
                period: '2019 - 2020',
                description: [
                    'Developed Python scripts for data analysis',
                    'Created web scrapers for market research',
                    'Collaborated with team on Django projects',
                ],
            },
        ],
        education: [
            {
                degree: 'Bachelor of Science in Computer Science',
                institution: 'University Name',
                year: '2019',
            },
        ],
    },
    skills: {
        categories: {
            web: {
                name: 'Web Development',
                icon: 'globe',
                skills: [
                    { name: 'Django', level: 5, icon: 'django' },
                    { name: 'Flask', level: 5, icon: 'flask' },
                    { name: 'FastAPI', level: 4, icon: 'fastapi' },
                    { name: 'REST APIs', level: 5, icon: 'api' },
                    { name: 'PostgreSQL', level: 4, icon: 'database' },
                    { name: 'SQLAlchemy', level: 4, icon: 'database' },
                    { name: 'Docker', level: 3, icon: 'docker' },
                ],
            },
            data: {
                name: 'Data Science',
                icon: 'bar-chart',
                skills: [
                    { name: 'Pandas', level: 5, icon: 'data' },
                    { name: 'NumPy', level: 5, icon: 'data' },
                    { name: 'Scikit-learn', level: 4, icon: 'brain' },
                    { name: 'Matplotlib', level: 4, icon: 'chart' },
                    { name: 'Jupyter', level: 5, icon: 'code' },
                    { name: 'TensorFlow', level: 3, icon: 'brain' },
                ],
            },
            automation: {
                name: 'Automation',
                icon: 'zap',
                skills: [
                    { name: 'Selenium', level: 5, icon: 'browser' },
                    { name: 'BeautifulSoup', level: 5, icon: 'code' },
                    { name: 'Requests', level: 5, icon: 'network' },
                    { name: 'Celery', level: 4, icon: 'clock' },
                    { name: 'APScheduler', level: 4, icon: 'clock' },
                    { name: 'Python Scripting', level: 5, icon: 'terminal' },
                ],
            },
        },
    },
    projects: {
        projects: [
            {
                id: '1',
                title: 'E-Commerce Platform',
                description: 'Full-stack e-commerce platform built with Django REST Framework and React. Features include user authentication, product management, shopping cart, payment integration, and order tracking.',
                technologies: ['Django', 'Django REST Framework', 'PostgreSQL', 'React', 'Stripe'],
                category: 'web',
                liveUrl: 'https://demo-project.com',
                githubUrl: 'https://github.com/yourusername/ecommerce',
                featured: true,
            },
            {
                id: '2',
                title: 'Data Analytics Dashboard',
                description: 'Interactive dashboard for visualizing and analyzing sales data. Built with Flask, Pandas, and Plotly. Provides real-time insights and customizable reports.',
                technologies: ['Flask', 'Pandas', 'Plotly', 'JavaScript', 'SQLite'],
                category: 'data',
                liveUrl: 'https://dashboard-demo.com',
                githubUrl: 'https://github.com/yourusername/dashboard',
                featured: true,
            },
            {
                id: '3',
                title: 'Web Scraper Suite',
                description: 'Automated web scraping tool for collecting data from multiple e-commerce sites. Includes scheduling, data cleaning, and export to CSV/JSON.',
                technologies: ['Selenium', 'BeautifulSoup', 'Celery', 'Redis', 'PostgreSQL'],
                category: 'automation',
                githubUrl: 'https://github.com/yourusername/scraper',
                featured: true,
            },
            {
                id: '4',
                title: 'Task Management API',
                description: 'RESTful API for task management with user authentication, team collaboration, and real-time updates. Built with FastAPI and PostgreSQL.',
                technologies: ['FastAPI', 'PostgreSQL', 'JWT', 'Docker', 'Redis'],
                category: 'web',
                githubUrl: 'https://github.com/yourusername/task-api',
                featured: false,
            },
            {
                id: '5',
                title: 'Machine Learning Pipeline',
                description: 'End-to-end ML pipeline for customer churn prediction. Includes data preprocessing, model training, and deployment with Flask API.',
                technologies: ['Scikit-learn', 'Pandas', 'Flask', 'Docker', 'Airflow'],
                category: 'data',
                githubUrl: 'https://github.com/yourusername/ml-pipeline',
                featured: false,
            },
            {
                id: '6',
                title: 'Social Media Automation',
                description: 'Automated tool for scheduling and posting content across multiple social media platforms. Includes analytics and reporting features.',
                technologies: ['Python', 'Requests', 'Celery', 'PostgreSQL', 'React'],
                category: 'automation',
                githubUrl: 'https://github.com/yourusername/social-automation',
                featured: false,
            },
        ],
    },
    contact: {
        email: 'your.email@example.com',
        location: 'San Francisco, CA',
        socialLinks: [
            { platform: 'GitHub', url: 'https://github.com/yourusername', icon: 'github' },
            { platform: 'LinkedIn', url: 'https://linkedin.com/in/yourusername', icon: 'linkedin' },
            { platform: 'Twitter', url: 'https://twitter.com/yourusername', icon: 'twitter' },
        ],
    },
    social: {
        github: 'https://github.com/yourusername',
        linkedin: 'https://linkedin.com/in/yourusername',
        twitter: 'https://twitter.com/yourusername',
    },
};
