import React from 'react';
import { Button } from '../ui/Button';
import { portfolioData } from '../../data/content';

export const Hero: React.FC = () => {
    const { hero } = portfolioData;

    return (
        <section className="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 via-white to-secondary-50 pt-16">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="text-center">
                    <h1 className="text-5xl md:text-7xl font-bold text-gray-900 mb-6">
                        {hero.name}
                    </h1>
                    <p className="text-2xl md:text-3xl font-semibold text-primary-600 mb-4">
                        {hero.title}
                    </p>
                    <p className="text-xl text-gray-600 mb-8 max-w-3xl mx-auto">
                        {hero.subtitle}
                    </p>
                    <p className="text-lg text-gray-700 mb-12 max-w-2xl mx-auto">
                        {hero.description}
                    </p>

                    <div className="flex flex-col sm:flex-row gap-4 justify-center mb-12">
                        <Button
                            variant="primary"
                            size="lg"
                            href={hero.ctaButtons.primary.link}
                        >
                            {hero.ctaButtons.primary.text}
                        </Button>
                        <Button
                            variant="secondary"
                            size="lg"
                            href={hero.ctaButtons.secondary.link}
                        >
                            {hero.ctaButtons.secondary.text}
                        </Button>
                    </div>

                    {/* Social Links */}
                    <div className="flex justify-center gap-6">
                        {hero.socialLinks.map((social) => (
                            <a
                                key={social.platform}
                                href={social.url}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="text-gray-600 hover:text-primary-600 transition-colors"
                                aria-label={social.platform}
                            >
                                <svg className="w-8 h-8" fill="currentColor" viewBox="0 0 24 24">
                                    {social.icon === 'github' && (
                                        <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84-1.235 1.911-1.235 3.221 0 4.609 2.807 8.523 6.989 10.477.438-1.352.723-2.787.723-4.284 0-3.627-2.373-6.627-5.627-7.635 0-1.089.226-2.127.634-3.074-.221-.533-.415-1.089-.577-1.671-.006-.052-.012-.104-.018-.156-.053-.493-.098-.998-.098-1.514 0-3.626 2.374-6.627 5.627-7.635z" />
                                    )}
                                    {social.icon === 'linkedin' && (
                                        <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z" />
                                    )}
                                    {social.icon === 'twitter' && (
                                        <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105 9.842 9.842 0 01-2.687.718 4.917 4.917 0 00-4.224-2.877c0-.093.012-.185.027-.275a4.931 4.931 0 00.921-3.022 4.816 4.816 0 01-1.56.596c.056 1.229.825 2.357 1.951 2.945a4.901 4.901 0 00-2.004.053z" />
                                    )}
                                </svg>
                            </a>
                        ))}
                    </div>
                </div>
            </div>
        </section>
    );
};
