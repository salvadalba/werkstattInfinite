import React, { useState } from 'react';
import { Section } from '../ui/Section';
import { Card } from '../ui/Card';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import { portfolioData } from '../../data/content';

export const Projects: React.FC = () => {
    const { projects } = portfolioData;
    const [activeFilter, setActiveFilter] = useState<string>('all');

    const categories = ['all', 'web', 'data', 'automation'] as const;

    const filteredProjects =
        activeFilter === 'all'
            ? projects.projects
            : projects.projects.filter((p) => p.category === activeFilter);

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

    return (
        <Section
            id="projects"
            title="Projects"
            subtitle="A selection of my work showcasing Python development across web, data, and automation"
            background="white"
        >
            {/* Filter Buttons */}
            <div className="flex flex-wrap justify-center gap-3 mb-12">
                {categories.map((category) => (
                    <button
                        key={category}
                        onClick={() => setActiveFilter(category)}
                        className={`px-6 py-2 rounded-full font-medium transition-colors ${activeFilter === category
                                ? 'bg-primary-600 text-white'
                                : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                            }`}
                    >
                        {category.charAt(0).toUpperCase() + category.slice(1)}
                    </button>
                ))}
            </div>

            {/* Projects Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {filteredProjects.map((project) => (
                    <Card key={project.id} className="flex flex-col h-full">
                        {/* Project Image */}
                        {project.image && (
                            <div className="h-48 bg-gradient-to-br from-primary-100 to-secondary-100 flex items-center justify-center">
                                <span className="text-4xl">📁</span>
                            </div>
                        )}

                        {/* Project Content */}
                        <div className="p-6 flex-1 flex flex-col">
                            <div className="flex items-start justify-between mb-3">
                                <Badge
                                    variant={getCategoryColor(project.category)}
                                    size="sm"
                                >
                                    {project.category.charAt(0).toUpperCase() + project.category.slice(1)}
                                </Badge>
                                {project.featured && (
                                    <Badge variant="success" size="sm">
                                        Featured
                                    </Badge>
                                )}
                            </div>

                            <h3 className="text-xl font-bold text-gray-900 mb-2">
                                {project.title}
                            </h3>

                            <p className="text-gray-600 mb-4 flex-1">
                                {project.description}
                            </p>

                            {/* Technologies */}
                            <div className="flex flex-wrap gap-2 mb-4">
                                {project.technologies.map((tech) => (
                                    <Badge key={tech} variant="gray" size="sm">
                                        {tech}
                                    </Badge>
                                ))}
                            </div>

                            {/* Action Buttons */}
                            <div className="flex gap-3 mt-auto">
                                {project.liveUrl && (
                                    <Button
                                        variant="primary"
                                        size="sm"
                                        href={project.liveUrl}
                                        className="flex-1"
                                    >
                                        Live Demo
                                    </Button>
                                )}
                                {project.githubUrl && (
                                    <Button
                                        variant="outline"
                                        size="sm"
                                        href={project.githubUrl}
                                        className="flex-1"
                                    >
                                        GitHub
                                    </Button>
                                )}
                            </div>
                        </div>
                    </Card>
                ))}
            </div>

            {/* Empty State */}
            {filteredProjects.length === 0 && (
                <div className="text-center py-12">
                    <p className="text-gray-600 text-lg">
                        No projects found in this category.
                    </p>
                </div>
            )}
        </Section>
    );
};
