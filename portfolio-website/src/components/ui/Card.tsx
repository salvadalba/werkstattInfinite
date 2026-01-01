import React from 'react';

interface CardProps {
    children: React.ReactNode;
    className?: string;
    hover?: boolean;
    onClick?: () => void;
}

export const Card: React.FC<CardProps> = ({
    children,
    className = '',
    hover = true,
    onClick,
}) => {
    const baseStyles = 'bg-white rounded-xl shadow-lg overflow-hidden';
    const hoverStyles = hover ? 'hover:shadow-xl hover:scale-105 transition-all duration-300' : '';
    const clickStyles = onClick ? 'cursor-pointer' : '';

    const classes = `${baseStyles} ${hoverStyles} ${clickStyles} ${className}`;

    return (
        <div className={classes} onClick={onClick}>
            {children}
        </div>
    );
};
