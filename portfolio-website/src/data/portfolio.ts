export interface PortfolioData {
    hero: HeroData;
    about: AboutData;
    skills: SkillsData;
    projects: ProjectsData;
    contact: ContactData;
    social: SocialData;
}

export interface HeroData {
    name: string;
    title: string;
    subtitle: string;
    description: string;
    ctaButtons: {
        primary: { text: string; link: string };
        secondary: { text: string; link: string };
    };
    socialLinks: Array<{ platform: string; url: string; icon: string }>;
}

export interface AboutData {
    bio: string[];
    experience: Array<{
        role: string;
        company: string;
        period: string;
        description: string[];
    }>;
    education: Array<{
        degree: string;
        institution: string;
        year: string;
    }>;
}

export interface Skill {
    name: string;
    level: number; // 1-5
    icon?: string;
}

export interface SkillsData {
    categories: {
        web: {
            name: string;
            icon: string;
            skills: Skill[];
        };
        data: {
            name: string;
            icon: string;
            skills: Skill[];
        };
        automation: {
            name: string;
            icon: string;
            skills: Skill[];
        };
    };
}

export interface Project {
    id: string;
    title: string;
    description: string;
    image?: string;
    technologies: string[];
    category: 'web' | 'data' | 'automation';
    liveUrl?: string;
    githubUrl?: string;
    featured: boolean;
}

export interface ProjectsData {
    projects: Project[];
}

export interface ContactData {
    email: string;
    location: string;
    socialLinks: Array<{ platform: string; url: string; icon: string }>;
}

export interface SocialData {
    github: string;
    linkedin: string;
    twitter?: string;
}
