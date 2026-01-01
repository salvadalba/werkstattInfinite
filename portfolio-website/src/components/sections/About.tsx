import React from 'react';
import { Section } from '../ui/Section';
import { portfolioData } from '../../data/content';

export const About: React.FC = () => {
    const { about } = portfolioData;

    return (
        <Section id="about" title="About Me" background="gray">
            <div className="max-w-4xl mx-auto">
                {/* Bio */}
                <div className="mb-16">
                    {about.bio.map((paragraph, index) => (
                        <p
                            key={index}
                            className="text-lg text-gray-700 mb-4 leading-relaxed"
                        >
                            {paragraph}
                        </p>
                    ))}
                </div>

                {/* Experience */}
                <div className="mb-16">
                    <h3 className="text-2xl font-bold text-gray-900 mb-8">
                        Experience
                    </h3>
                    <div className="space-y-8">
                        {about.experience.map((exp, index) => (
                            <div
                                key={index}
                                className="border-l-4 border-primary-600 pl-6 relative"
                            >
                                <div className="absolute -left-2 top-0 w-4 h-4 bg-primary-600 rounded-full"></div>
                                <h4 className="text-xl font-semibold text-gray-900 mb-1">
                                    {exp.role}
                                </h4>
                                <p className="text-primary-600 font-medium mb-2">
                                    {exp.company} • {exp.period}
                                </p>
                                <ul className="list-disc list-inside space-y-1 text-gray-700">
                                    {exp.description.map((item, i) => (
                                        <li key={i}>{item}</li>
                                    ))}
                                </ul>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Education */}
                <div>
                    <h3 className="text-2xl font-bold text-gray-900 mb-8">
                        Education
                    </h3>
                    <div className="space-y-4">
                        {about.education.map((edu, index) => (
                            <div
                                key={index}
                                className="bg-white p-6 rounded-lg shadow-md"
                            >
                                <h4 className="text-xl font-semibold text-gray-900 mb-2">
                                    {edu.degree}
                                </h4>
                                <p className="text-gray-700">{edu.institution}</p>
                                <p className="text-gray-600 text-sm mt-1">{edu.year}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </Section>
    );
};
