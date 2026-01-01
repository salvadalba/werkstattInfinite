# Python Developer Portfolio Website

A modern, responsive single-page portfolio website for Python software developers, built with React, Vite, and Tailwind CSS.

## Features

- 🎨 Modern, clean design with smooth animations
- 📱 Fully responsive (mobile, tablet, desktop)
- 🚀 Fast loading with Vite
- 🎯 Smooth scrolling navigation with active section highlighting
- 💻 Showcases Python expertise in:
  - Web Development (Django, Flask, FastAPI)
  - Data Science (Pandas, NumPy, Scikit-learn)
  - Automation (Selenium, BeautifulSoup, Celery)
- 📁 Filterable project gallery
- 📧 Contact form with social media integration
- 🎨 Easy to customize with centralized data structure

## Tech Stack

- **Frontend**: React 18 with TypeScript
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **Icons**: Inline SVG icons

## Getting Started

### Prerequisites

- Node.js 18+ and npm

### Installation

1. Navigate to the project directory:

```bash
cd portfolio-website
```

1. Install dependencies:

```bash
npm install
```

1. Start the development server:

```bash
npm run dev
```

1. Open [http://localhost:5173](http://localhost:5173) in your browser

## Customization

### Personal Information

Edit `src/data/content.ts` to customize your portfolio:

```typescript
export const portfolioData: PortfolioData = {
  hero: {
    name: 'Your Name',              // Your name
    title: 'Python Software Developer',  // Your title
    subtitle: 'Your subtitle',        // Short description
    description: 'Your bio...',       // Longer description
    // ... customize CTA buttons and social links
  },
  about: {
    bio: ['Your bio paragraphs...'],
    experience: [/* your work experience */],
    education: [/* your education */],
  },
  skills: {
    categories: {
      web: { /* web development skills */ },
      data: { /* data science skills */ },
      automation: { /* automation skills */ },
    },
  },
  projects: {
    projects: [/* your projects */],
  },
  contact: {
    email: 'your.email@example.com',
    location: 'Your City, Country',
    socialLinks: [/* your social links */],
  },
};
```

### Adding Projects

Add projects to the `projects.projects` array:

```typescript
{
  id: 'unique-id',
  title: 'Project Name',
  description: 'Project description...',
  technologies: ['Django', 'React', 'PostgreSQL'],
  category: 'web',  // 'web' | 'data' | 'automation'
  liveUrl: 'https://demo-project.com',  // Optional
  githubUrl: 'https://github.com/username/repo',  // Optional
  featured: true,  // Show "Featured" badge
}
```

### Updating Skills

Modify skills in `skills.categories`:

```typescript
{
  name: 'Skill Name',
  level: 4,  // 1-5 (proficiency level)
  icon: 'icon-name',  // Optional icon identifier
}
```

## Building for Production

```bash
npm run build
```

The built files will be in the `dist` directory.

## Preview Production Build

```bash
npm run preview
```

## Deployment

### Vercel (Recommended)

1. Push your code to GitHub
2. Import your repository in [Vercel](https://vercel.com)
3. Vercel will automatically detect Vite and deploy

### Netlify

1. Run `npm run build`
2. Drag the `dist` folder to [Netlify Drop](https://app.netlify.com/drop)

### GitHub Pages

1. Update `vite.config.ts` to set base path:

```typescript
export default defineConfig({
  base: '/your-repo-name',
  // ...
})
```

1. Run `npm run build`
2. Push `dist` folder to `gh-pages` branch

## Contact Form Integration

The contact form currently simulates submission. To make it functional:

### Option 1: Formspree (Easiest)

1. Sign up at [formspree.io](https://formspree.io)
2. Create a new form
3. Update the form action in `Contact.tsx`:

```typescript
<form action="https://formspree.io/f/your-form-id" method="POST">
```

### Option 2: EmailJS

1. Sign up at [emailjs.com](https://www.emailjs.com)
2. Follow their React integration guide
3. Update the `handleSubmit` function in `Contact.tsx`

### Option 3: Backend Service

Create a Python backend (Flask/FastAPI) with an email endpoint and update `handleSubmit` to call your API.

## Project Structure

```
portfolio-website/
├── public/              # Static assets
├── src/
│   ├── components/
│   │   ├── layout/    # Navbar, Footer
│   │   ├── sections/   # Hero, About, Skills, Projects, Contact
│   │   └── ui/        # Reusable UI components
│   ├── data/            # Portfolio content data
│   ├── hooks/           # Custom React hooks
│   ├── styles/          # Additional styles
│   ├── App.tsx          # Main app component
│   ├── main.tsx         # Entry point
│   └── index.css        # Global styles
├── index.html           # HTML template
├── package.json         # Dependencies
├── tailwind.config.js   # Tailwind configuration
├── tsconfig.json       # TypeScript configuration
└── vite.config.ts      # Vite configuration
```

## Customizing Colors

Edit `tailwind.config.js` to change the color scheme:

```javascript
theme: {
  extend: {
    colors: {
      primary: { /* your primary colors */ },
      secondary: { /* your secondary colors */ },
    },
  },
}
```

## Performance Tips

- Images are lazy-loaded automatically by browsers
- Code splitting is handled by Vite
- Use WebP format for images
- Minimize bundle size by removing unused dependencies

## Accessibility

- Semantic HTML elements throughout
- ARIA labels on interactive elements
- Keyboard navigation support
- Focus indicators on all interactive elements
- Color contrast ratios meet WCAG AA standards

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

This project is open source and available for personal and commercial use.

## Support

For issues or questions, please open an issue on the GitHub repository.
