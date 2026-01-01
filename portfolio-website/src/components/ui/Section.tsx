import React from 'react';

interface SectionProps {
    id?: string;
    title: string;
    subtitle?: string;
    children: React.ReactNode;
    className?: string;
    background?: 'white' | 'gray';
}

export const Section: React.FC<SectionProps> = ({
    id,
    title,
    subtitle,
    children,
    className = '',
    background = 'white',
}) => {
    const backgroundStyles = background === 'gray' ? 'bg-gray-50' : 'bg-white';

    return (
        <section
            id={id}
            className={`py-20 ${backgroundStyles} ${className}`}
        >
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="text-center mb-12">
                    <h2 className="text-4xl font-bold text-gray-900 mb-4">{title}</h2>
                    {subtitle && (
                        <p className="text-xl text-gray-600 max-w-2xl mx-auto">{subtitle}</p>
                    )}
                </div>
                {children}
            </div>
        </section>
    );
};
