import React, { useState } from 'react';
import { Section } from '../ui/Section';
import { Badge } from '../ui/Badge';
import { Card } from '../ui/Card';
import { portfolioData } from '../../data/content';

export const Skills: React.FC = () => {
    const { skills } = portfolioData;
    const [activeCategory, setActiveCategory] = useState<string | null>(null);

    const categories = Object.entries(skills.categories) as [
        string,
        { name: string; icon: string; skills: Array<{ name: string; level: number }> }
    ][];

    const filteredCategories = activeCategory
        ? categories.filter(([key]) => key === activeCategory)
        : categories;

    const getCategoryColor = (category: string) => {
        switch (category) {
            case 'web':
                return 'primary';
            case 'data':
                return 'secondary';
            case 'automation':
                return 'success';
            default:
                return 'gray';
        }
    };

    const renderSkillLevel = (level: number) => {
        return (
            <div className="flex gap-1 mt-2">
                {[1, 2, 3, 4, 5].map((i) => (
                    <div
                        key={i}
                        className={`h-2 w-8 rounded-full transition-colors ${i <= level
                                ? 'bg-primary-600'
                                : 'bg-gray-200'
                            }`}
                    />
                ))}
            </div>
        );
    };

    return (
        <Section
            id="skills"
            title="Skills & Expertise"
            subtitle="Proficient in web development, data science, and automation with Python"
            background="gray"
        >
            {/* Category Filter */}
            <div className="flex flex-wrap justify-center gap-3 mb-12">
                <button
                    onClick={() => setActiveCategory(null)}
                    className={`px-6 py-2 rounded-full font-medium transition-colors ${activeCategory === null
                            ? 'bg-primary-600 text-white'
                            : 'bg-white text-gray-700 hover:bg-gray-100'
                        }`}
                >
                    All
                </button>
                {categories.map(([key, category]) => (
                    <button
                        key={key}
                        onClick={() => setActiveCategory(activeCategory === key ? null : key)}
                        className={`px-6 py-2 rounded-full font-medium transition-colors ${activeCategory === key
                                ? 'bg-primary-600 text-white'
                                : 'bg-white text-gray-700 hover:bg-gray-100'
                            }`}
                    >
                        {category.name}
                    </button>
                ))}
            </div>

            {/* Skills Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {filteredCategories.map(([key, category]) => (
                    <Card key={key} className="p-6">
                        <div className="flex items-center gap-3 mb-4">
                            <svg
                                className="w-8 h-8 text-primary-600"
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                            >
                                {category.icon === 'globe' && (
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9m-9 9a9 9 0 01-9 9m9 9c1.657 0 3 4.03 3 9s-1.343 9-3 9"
                                    />
                                )}
                                {category.icon === 'bar-chart' && (
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"
                                    />
                                )}
                                {category.icon === 'zap' && (
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M13 10V3L4 14h7v7l9-11h-7z"
                                    />
                                )}
                            </svg>
                            <h3 className="text-xl font-bold text-gray-900">
                                {category.name}
                            </h3>
                        </div>
                        <div className="space-y-4">
                            {category.skills.map((skill, index) => (
                                <div key={index}>
                                    <div className="flex items-center justify-between mb-1">
                                        <span className="font-medium text-gray-900">
                                            {skill.name}
                                        </span>
                                        <Badge variant={getCategoryColor(key)} size="sm">
                                            Level {skill.level}
                                        </Badge>
                                    </div>
                                    {renderSkillLevel(skill.level)}
                                </div>
                            ))}
                        </div>
                    </Card>
                ))}
            </div>
        </Section>
    );
};
